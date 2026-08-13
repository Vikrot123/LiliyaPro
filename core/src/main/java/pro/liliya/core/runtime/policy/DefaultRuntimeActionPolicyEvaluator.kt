package pro.liliya.core.runtime.policy

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.control.RuntimeCommand

class DefaultRuntimeActionPolicyEvaluator :
    RuntimeActionPolicyEvaluator {

    override fun evaluate(
        request: RuntimeActionRequest
    ): RuntimeActionPolicyResult {

        return when (request.command) {

            RuntimeCommand.HEALTH_CHECK ->
                RuntimeActionPolicyResult(
                    decision = RuntimeActionPolicyDecision.ALLOW,
                    reason = "Health check is safe runtime operation",
                    policyId = "health-check-safe"
                )

            else ->
                RuntimeActionPolicyResult(
                    decision = RuntimeActionPolicyDecision.DENY,
                    reason = "Action requires explicit runtime authority",
                    policyId = "default-deny"
                )
        }
    }
}
