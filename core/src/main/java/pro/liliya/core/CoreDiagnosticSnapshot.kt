package pro.liliya.core

import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.RuntimeServiceState

data class CoreDiagnosticSnapshot(
    val runtimeState: CoreRuntimeState,
    val moduleStates: Map<String, ModuleState>,
    val runtimeServiceStates: Map<String, RuntimeServiceState> = emptyMap(),
    val failureReason: String? = null
)
