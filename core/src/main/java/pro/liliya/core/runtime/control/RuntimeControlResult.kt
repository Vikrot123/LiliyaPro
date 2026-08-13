package pro.liliya.core.runtime.control

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot

data class RuntimeControlResult(
    val command: RuntimeCommand,
    val success: Boolean,
    val previousState: CoreRuntimeState,
    val currentState: CoreRuntimeState,
    val status: RuntimeStatusSnapshot,
    val message: String
)
