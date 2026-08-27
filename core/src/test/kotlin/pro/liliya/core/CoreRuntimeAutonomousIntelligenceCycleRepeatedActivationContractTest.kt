package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCycleRepeatedActivationContractTest {

    @Test
    fun repeated_autonomous_activation_is_independent_within_running_runtime() {
        CoreRuntime.stop()
        CoreRuntime.start()

        try {
            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val firstSource =
                "v1.503-first"

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

            val secondSource =
                "v1.503-second"

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

            assertNotSame(
                first,
                second,
                "repeated activation must produce a distinct cycle result"
            )

            assertNotSame(
                first.executionCycle.execution,
                second.executionCycle.execution,
                "repeated activation must produce a distinct execution result"
            )

            assertSame(
                first.intelligence,
                first.executionCycle.execution.intelligence
            )

            assertSame(
                second.intelligence,
                second.executionCycle.execution.intelligence
            )

            val firstRequest =
                first
                    .executionCycle
                    .execution
                    .execution
                    .request

            if (firstRequest != null) {
                assertEquals(
                    firstSource,
                    firstRequest.source
                )

                assertSame(
                    firstAuthority,
                    firstRequest.authority
                )
            }

            val secondRequest =
                second
                    .executionCycle
                    .execution
                    .execution
                    .request

            if (secondRequest != null) {
                assertEquals(
                    secondSource,
                    secondRequest.source
                )

                assertSame(
                    secondAuthority,
                    secondRequest.authority
                )
            }

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "repeated activation must not disturb runtime lifecycle state"
            )
        } finally {
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )
    }
}
