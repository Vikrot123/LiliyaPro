package pro.liliya.core

import pro.liliya.core.runtime.RuntimeRecoverySnapshot
import pro.liliya.core.runtime.RuntimeServiceFailure
import pro.liliya.core.runtime.RuntimeServiceHealth
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.module.ModuleState

data class CoreRuntimeDiagnosticsSnapshot(
    val runtimeState: CoreRuntimeState,
    val moduleStates: Map<String, ModuleState>,
    val serviceStates: Map<String, RuntimeServiceState>,
    val serviceFailures: List<RuntimeServiceFailure>,
    val serviceHealth: Map<String, RuntimeServiceHealth>,
    val recoverySnapshot: RuntimeRecoverySnapshot?,
    val failureReason: String?
)
