package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCycleRestartContractTest {

    @Test
    fun autonomous_intelligence_activation_survives_runtime_restart() {
        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )

        val rejectedSource =
            "v1.502-stopped"

        assertFailsWith<IllegalStateException> {
            CoreRuntime.processAutonomousIntelligenceCycle(
                source =
                    rejectedSource,
                authority =
                    RuntimeActionAuthorityContext(
                        source =
                            rejectedSource,
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )
            )
        }

        CoreRuntime.start()

        try {
            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val firstSource =
                "v1.502-first-running"

            val firstAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        firstSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            val first =
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        firstSource,
                    authority =
                        firstAuthority
                )

            assertSame(
                first.intelligence,
                first.executionCycle.execution.intelligence
            )

            CoreRuntime.stop()

            assertEquals(
                CoreRuntimeState.STOPPED,
                CoreRuntime.state()
            )

            val stoppedAudit =
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size

            assertFailsWith<IllegalStateException> {
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        "v1.502-between-runs",
                    authority =
                        RuntimeActionAuthorityContext(
                            source =
                                "v1.502-between-runs",
                            level =
                                RuntimeAuthorityLevel.SYSTEM
                        )
                )
            }

            assertEquals(
                stoppedAudit,
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size,
                "stopped activation must not reach action dispatch"
            )

            CoreRuntime.start()

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val secondSource =
                "v1.502-second-running"

            val secondAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        secondSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            val second =
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        secondSource,
                    authority =
                        secondAuthority
                )

            assertSame(
                second.intelligence,
                second.executionCycle.execution.intelligence
            )

            val request =
                second
                    .executionCycle
                    .execution
                    .execution
                    .request

            if (request != null) {
                assertEquals(
                    secondSource,
                    request.source
                )

                assertSame(
                    secondAuthority,
                    request.authority
                )
            }
        } finally {
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )
    }
}
