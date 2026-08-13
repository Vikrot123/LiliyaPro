package pro.liliya.core.runtime.policy

data class RuntimeActionPolicyResult(
    val decision: RuntimeActionPolicyDecision,
    val reason: String,
    val policyId: String
)
