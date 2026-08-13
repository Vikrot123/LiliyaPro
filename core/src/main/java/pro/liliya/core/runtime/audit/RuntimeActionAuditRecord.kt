package pro.liliya.core.runtime.audit

import pro.liliya.core.runtime.action.RuntimeActionRequest

data class RuntimeActionAuditRecord(
    val request: RuntimeActionRequest,
    val success: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
