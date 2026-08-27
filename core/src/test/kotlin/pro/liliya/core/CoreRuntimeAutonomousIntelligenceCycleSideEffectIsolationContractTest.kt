package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCycleSideEffectIsolationContractTest {

    @Test
    fun repeated_autonomous_cycles_keep_action_side_effects_isolated() {
        CoreRuntime.stop()
        CoreRuntime.start()

        try {
            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val auditBefore =
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size

            val firstSource =
                "v1.506-first"

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

            val firstRequest =
                first
                    .executionCycle
                    .execution
                    .execution
                    .request

            val auditAfterFirst =
                CoreRuntime.getRuntimeActionAudit()

            val firstDelta =
                auditAfterFirst.drop(
                    auditBefore
                )

            if (firstRequest != null) {
                assertEquals(
                    firstSource,
                    firstRequest.source
                )

                assertSame(
                    firstAuthority,
                    firstRequest.authority
                )

                assertTrue(
                    firstDelta.any {
                        it.request === firstRequest
                    },
                    "first autonomous request must be audited by exact identity"
                )
            } else {
                assertTrue(
                    firstDelta.isEmpty(),
                    "NO_ACTION first cycle must not invent action audit records"
                )
            }

            val secondAuditStart =
                auditAfterFirst.size

            val secondSource =
                "v1.506-second"

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

            val secondRequest =
                second
                    .executionCycle
                    .execution
                    .execution
                    .request

            val auditAfterSecond =
                CoreRuntime.getRuntimeActionAudit()

            val secondDelta =
                auditAfterSecond.drop(
                    secondAuditStart
                )

            if (secondRequest != null) {
                assertEquals(
                    secondSource,
                    secondRequest.source
                )

                assertSame(
                    secondAuthority,
                    secondRequest.authority
                )

                assertTrue(
                    secondDelta.any {
                        it.request === secondRequest
                    },
                    "second autonomous request must be audited by exact identity"
                )
            } else {
                assertTrue(
                    secondDelta.isEmpty(),
                    "NO_ACTION second cycle must not invent action audit records"
                )
            }

            if (
                firstRequest != null &&
                secondRequest != null
            ) {
                assertNotSame(
                    firstRequest,
                    secondRequest,
                    "separate autonomous cycles must own distinct action requests"
                )

                assertTrue(
                    secondDelta.none {
                        it.request === firstRequest
                    },
                    "second cycle must not re-audit first cycle request"
                )

                assertTrue(
                    firstDelta.none {
                        it.request === secondRequest
                    },
                    "first cycle audit delta must not contain second cycle request"
                )
            }

            assertSame(
                first.intelligence,
                first.executionCycle.execution.intelligence
            )

            assertSame(
                second.intelligence,
                second.executionCycle.execution.intelligence
            )

            assertNotSame(
                first.executionCycle.execution,
                second.executionCycle.execution,
                "separate cycles must retain independent execution snapshots"
            )

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "side-effect isolation verification must not disturb runtime state"
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
