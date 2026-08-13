package pro.liliya.core.runtime.policy

import pro.liliya.core.runtime.action.RuntimeActionRequest

interface RuntimeActionPolicyEvaluator {

    fun evaluate(
        request: RuntimeActionRequest
    ): RuntimeActionPolicyResult
}
