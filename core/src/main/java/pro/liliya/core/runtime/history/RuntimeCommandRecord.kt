package pro.liliya.core.runtime.history

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.control.RuntimeCommand

data class RuntimeCommandRecord(
    val command: RuntimeCommand,
    val success: Boolean,
    val previousState: CoreRuntimeState,
    val currentState: CoreRuntimeState,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
