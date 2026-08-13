package pro.liliya.core.runtime.policy

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

data class RuntimeActionPolicyResult(
    val decision: RuntimeActionPolicyDecision,
    val reason: String,
    val policyId: String,
    val authoritySource: String? = null,
    val authorityLevel: RuntimeAuthorityLevel? = null,
    val capabilityAllowed: Boolean? = null,
    val capabilityDescription: String? = null
)
