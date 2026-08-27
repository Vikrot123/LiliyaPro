package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCycleFailedStateContractTest {

    @Test
    fun failed_runtime_rejects_autonomous_intelligence_cycle_before_activation() {
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

            val actionAuditBefore =
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size

            val source =
                "v1.504-failed-runtime"

            val authority =
                RuntimeActionAuthorityContext(
                    source =
                        source,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            assertFailsWith<IllegalStateException> {
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        source,
                    authority =
                        authority
                )
            }

            assertEquals(
                CoreRuntimeState.FAILED,
                CoreRuntime.state(),
                "rejected autonomous activation must preserve FAILED state"
            )

            assertEquals(
                actionAuditBefore,
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size,
                "rejected autonomous activation must not dispatch runtime actions"
            )
        } finally {
            CoreRuntime.resetModuleProvider()
            CoreRuntime.stop()
            CoreRuntime.start()
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state(),
            "test cleanup must restore STOPPED runtime"
        )
    }
}
