package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.RuntimeServiceState

class CoreRuntimeDiagnosticsMapperContractTest {

    @Test
    fun mapper_preserves_runtime_diagnostics() {

        val snapshot = CoreDiagnosticSnapshot(
            runtimeState = CoreRuntimeState.RUNNING,
            moduleStates = emptyMap(),
            runtimeServiceStates = mapOf(
                "service" to RuntimeServiceState.RUNNING
            ),
            runtimeRecoverySnapshot = RuntimeRecoverySnapshot(
                restartCounts = mapOf(
                    "service" to 2
                ),
                lastRecoveredService = "service",
                lastRecoverySuccessful = true
            ),
            failureReason = null
        )

        val result = snapshot.toRuntimeDiagnosticsSnapshot()

        assertEquals(
            CoreRuntimeState.RUNNING,
            result.runtimeState
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            result.serviceStates["service"]
        )

        assertEquals(
            2,
            result.recoverySnapshot
                ?.restartCounts
                ?.get("service")
        )

        assertTrue(
            result.recoverySnapshot
                ?.lastRecoverySuccessful == true
        )
    }
}
