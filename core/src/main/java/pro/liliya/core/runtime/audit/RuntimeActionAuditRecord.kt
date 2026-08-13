package pro.liliya.core.runtime.audit

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.policy.RuntimeActionPolicyDecision

data class RuntimeActionAuditRecord(
    val request: RuntimeActionRequest,
    val success: Boolean,
    val message: String,
    val policyId: String? = null,
    val policyDecision: RuntimeActionPolicyDecision? = null,
    val timestamp: Long = System.currentTimeMillis()
)
