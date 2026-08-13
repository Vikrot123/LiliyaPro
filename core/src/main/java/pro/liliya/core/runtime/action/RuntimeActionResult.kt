package pro.liliya.core.runtime.action

import pro.liliya.core.runtime.control.RuntimeControlResult

data class RuntimeActionResult(
    val request: RuntimeActionRequest,
    val success: Boolean,
    val controlResult: RuntimeControlResult,
    val timestamp: Long = System.currentTimeMillis()
)
