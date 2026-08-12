package pro.liliya.core

import pro.liliya.core.module.ModuleState

data class CoreDiagnosticSnapshot(
    val runtimeState: CoreRuntimeState,
    val moduleStates: Map<String, ModuleState>
)
