package pro.liliya.core

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeServiceHealth

data class CoreDiagnosticSnapshot(
    val runtimeState: CoreRuntimeState,
    val moduleStates: Map<String, ModuleState>,
    val runtimeServiceStates: Map<String, RuntimeServiceState> = emptyMap(),
    val runtimeServiceFailures: List<RuntimeServiceFailure> = emptyList(),
    val runtimeServiceHealth: Map<String, RuntimeServiceHealth> = emptyMap(),
    val failureReason: String? = null
) {

    fun isHealthy(): Boolean {
        return runtimeServiceHealth.values.all { it.healthy }
    }

    fun failedServices(): List<RuntimeServiceHealth> {
        return runtimeServiceHealth.values.filter { !it.healthy }
    }

    fun runningServices(): List<RuntimeServiceHealth> {
        return runtimeServiceHealth.values.filter {
            it.state == RuntimeServiceState.RUNNING
        }
    }
}
