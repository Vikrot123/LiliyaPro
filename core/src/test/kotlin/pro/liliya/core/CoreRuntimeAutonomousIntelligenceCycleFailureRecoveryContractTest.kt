package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCycleFailureRecoveryContractTest {

    @Test
    fun autonomous_intelligence_activation_recovers_after_failed_runtime() {
        CoreRuntime.resetModuleProvider()
        CoreRuntime.stop()

        try {
            CoreRuntime.setModuleProvider(
                FailingModuleProvider()
            )

            runCatching {
                CoreRuntime.start()
            }

            assertEquals(
                CoreRuntimeState.FAILED,
                CoreRuntime.state(),
                "startup failure must place runtime in FAILED state"
            )

            val failedAuditBefore =
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size

            val failedSource =
                "v1.505-failed"

            val failedAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        failedSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            assertFailsWith<IllegalStateException> {
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        failedSource,
                    authority =
                        failedAuthority
                )
            }

            assertEquals(
                CoreRuntimeState.FAILED,
                CoreRuntime.state(),
                "rejected activation must preserve FAILED state"
            )

            assertEquals(
                failedAuditBefore,
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size,
                "FAILED runtime must reject activation before action dispatch"
            )

            CoreRuntime.resetModuleProvider()
            CoreRuntime.start()

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "runtime must recover into RUNNING state"
            )

            val recoveredSource =
                "v1.505-recovered-running"

            val recoveredAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        recoveredSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            val recovered =
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        recoveredSource,
                    authority =
                        recoveredAuthority
                )

            assertSame(
                recovered.intelligence,
                recovered.executionCycle.execution.intelligence,
                "recovered autonomous cycle must preserve intelligence identity"
            )

            val recoveredRequest =
                recovered
                    .executionCycle
                    .execution
                    .execution
                    .request

            if (recoveredRequest != null) {
                assertEquals(
                    recoveredSource,
                    recoveredRequest.source,
                    "recovered cycle must preserve source"
                )

                assertSame(
                    recoveredAuthority,
                    recoveredRequest.authority,
                    "recovered cycle must preserve authority identity"
                )
            }

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "successful autonomous activation must preserve recovered RUNNING state"
            )
        } finally {
            CoreRuntime.resetModuleProvider()
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state(),
            "test cleanup must leave runtime STOPPED"
        )
    }
}
