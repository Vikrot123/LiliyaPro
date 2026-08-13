package pro.liliya.core.runtime.policy

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.control.RuntimeCommand

class DefaultRuntimeActionPolicyEvaluator : RuntimeActionPolicyEvaluator {

    override fun evaluate(
        request: RuntimeActionRequest
    ): RuntimeActionPolicyDecision {

        return when (request.command) {

            RuntimeCommand.HEALTH_CHECK ->
                RuntimeActionPolicyDecision.ALLOW

            else ->
                RuntimeActionPolicyDecision.DENY
        }
    }
}
