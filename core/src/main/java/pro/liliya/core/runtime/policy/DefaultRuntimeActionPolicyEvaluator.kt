package pro.liliya.core.runtime.policy

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class DefaultRuntimeActionPolicyEvaluator :
    RuntimeActionPolicyEvaluator {

    override fun evaluate(
        request: RuntimeActionRequest
    ): RuntimeActionPolicyResult {

        val authority = request.resolvedAuthority()

        return when (request.command) {

            RuntimeCommand.HEALTH_CHECK -> {
                when (authority.level) {

                    RuntimeAuthorityLevel.INTERNAL,
                    RuntimeAuthorityLevel.SYSTEM,
                    RuntimeAuthorityLevel.USER ->
                        RuntimeActionPolicyResult(
                            decision = RuntimeActionPolicyDecision.ALLOW,
                            reason = "Authority level permits health check",
                            policyId = "health-check-authority"
                        )

                    RuntimeAuthorityLevel.UNKNOWN ->
                        RuntimeActionPolicyResult(
                            decision = RuntimeActionPolicyDecision.DENY,
                            reason = "Unknown authority cannot execute runtime action",
                            policyId = "authority-required"
                        )
                }
            }

            else ->
                RuntimeActionPolicyResult(
                    decision = RuntimeActionPolicyDecision.DENY,
                    reason = "Action requires explicit runtime authority",
                    policyId = "default-deny"
                )
        }
    }
}
