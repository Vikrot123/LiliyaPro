package pro.liliya.core.runtime.monitor

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.CoreDiagnosticSnapshot
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.composition.RuntimeComposition

class DefaultRuntimeMonitorContractTest {

    private fun composition(
        diagnostics: CoreDiagnosticSnapshot
    ): RuntimeComposition {

        val base = DefaultRuntimeComposition()

        return object : RuntimeComposition by base {

            override fun createDiagnosticSnapshot():
                CoreDiagnosticSnapshot {
                return diagnostics
            }
        }
    }

    @Test
    fun monitor_reports_healthy_runtime() {

        val monitor = DefaultRuntimeMonitor(
            composition(
                CoreDiagnosticSnapshot(
                            runtimeState = CoreRuntimeState.RUNNING,
                            moduleStates = emptyMap(),
                            runtimeServiceStates = emptyMap(),
                            runtimeServiceFailures = emptyList(),
                            runtimeServiceHealth = mapOf(
                                "service" to RuntimeServiceHealth(
                                    name = "service",
                                    state = RuntimeServiceState.RUNNING,
                                    healthy = true
                                )
                            ),
                            runtimeRecoverySnapshot = null,
                            failureReason = null
                        )
            )
        )

        val result = monitor.snapshot()

        assertEquals(
            CoreRuntimeState.RUNNING,
            result.diagnostics.runtimeState
        )

        assertTrue(result.healthy)
    }


    @Test
    fun monitor_reports_unhealthy_runtime() {

        val monitor = DefaultRuntimeMonitor(
            composition(
                CoreDiagnosticSnapshot(
                            runtimeState = CoreRuntimeState.RUNNING,
                            moduleStates = emptyMap(),
                            runtimeServiceStates = emptyMap(),
                            runtimeServiceFailures = emptyList(),
                            runtimeServiceHealth = mapOf(
                                "failed-service" to RuntimeServiceHealth(
                                    name = "failed-service",
                                    state = RuntimeServiceState.FAILED,
                                    healthy = false
                                )
                            ),
                            runtimeRecoverySnapshot = null,
                            failureReason = "service failed"
                        )
            )
        )

        val result = monitor.snapshot()

        assertFalse(result.healthy)

        assertEquals(
            "service failed",
            result.diagnostics.failureReason
        )
    }
}
