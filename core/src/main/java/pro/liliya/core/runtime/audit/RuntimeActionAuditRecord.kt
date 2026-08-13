package pro.liliya.core.runtime.audit

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.policy.RuntimeActionPolicyDecision

data class RuntimeActionAuditRecord(
    val request: RuntimeActionRequest,
    val success: Boolean,
    val message: String,
    val policyId: String? = null,
    val policyDecision: RuntimeActionPolicyDecision? = null,
    val authoritySource: String? = null,
    val authorityLevel: RuntimeAuthorityLevel? = null,
    val capabilityAllowed: Boolean? = null,
    val capabilityDescription: String? = null,
    val requiredAuthority: RuntimeAuthorityLevel? = null,
    val actualAuthority: RuntimeAuthorityLevel? = null,
    val timestamp: Long = System.currentTimeMillis()
)
