package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.RuntimeServiceState

class DefaultCoreRuntimeDiagnosticsServiceContractTest {

    @Test
    fun service_returns_mapped_runtime_snapshot() {

        val provider = CoreDiagnosticProvider {
            CoreDiagnosticSnapshot(
                runtimeState = CoreRuntimeState.RUNNING,
                moduleStates = emptyMap(),
                runtimeServiceStates = mapOf(
                    "service" to RuntimeServiceState.RUNNING
                ),
                runtimeRecoverySnapshot = RuntimeRecoverySnapshot(
                    restartCounts = mapOf(
                        "service" to 3
                    ),
                    lastRecoveredService = "service",
                    lastRecoverySuccessful = true
                ),
                failureReason = null
            )
        }

        val service = DefaultCoreRuntimeDiagnosticsService(provider)

        val result = service.snapshot()

        assertEquals(
            CoreRuntimeState.RUNNING,
            result.runtimeState
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            result.serviceStates["service"]
        )

        assertNotNull(
            result.recoverySnapshot
        )

        assertEquals(
            3,
            result.recoverySnapshot
                ?.restartCounts
                ?.get("service")
        )
    }
}
