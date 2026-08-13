package pro.liliya.core.runtime.policy

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.RuntimeCapabilityResolver

class DefaultRuntimeActionPolicyEvaluator(
    private val capabilityResolver: RuntimeCapabilityResolver =
        RuntimeCapabilityResolver()
) : RuntimeActionPolicyEvaluator {

    override fun evaluate(
        request: RuntimeActionRequest
    ): RuntimeActionPolicyResult {

        val authority = request.resolvedAuthority()

        val capability = capabilityResolver.resolve(
            authority.level,
            request.command
        )

        return if (capability.allowed) {

            RuntimeActionPolicyResult(
                decision = RuntimeActionPolicyDecision.ALLOW,
                reason = capability.description,
                policyId = "capability-based-policy",
                authoritySource = authority.source,
                authorityLevel = authority.level,
                    capabilityAllowed = capability.allowed,
                    capabilityDescription = capability.description
            )

        } else {

            RuntimeActionPolicyResult(
                decision = RuntimeActionPolicyDecision.DENY,
                reason = capability.description,
                policyId = "capability-denied",
                authoritySource = authority.source,
                authorityLevel = authority.level,
                    capabilityAllowed = capability.allowed,
                    capabilityDescription = capability.description
            )
        }
    }
}
