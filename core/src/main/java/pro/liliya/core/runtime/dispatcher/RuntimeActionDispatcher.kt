package pro.liliya.core.runtime.dispatcher

import pro.liliya.core.CoreRuntime
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.audit.RuntimeActionAuditRecord
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.policy.RuntimeActionPolicyDecision
import pro.liliya.core.runtime.control.RuntimeControlResult

class RuntimeActionDispatcher(
    private val registry: RuntimeActionHandlerRegistry,
    private val auditProvider: RuntimeActionAuditProvider,
    private val policyEvaluator: RuntimeActionPolicyEvaluator
) {

    fun dispatch(
        request: RuntimeActionRequest
    ): RuntimeActionResult {

        val policyDecision = policyEvaluator.evaluate(request)

        if (policyDecision == RuntimeActionPolicyDecision.DENY) {

            val deniedResult = RuntimeActionResult(
                request = request,
                success = false,
                controlResult = RuntimeControlResult(
                    command = request.command,
                    success = false,
                    previousState = CoreRuntime.getRuntimeState(),
                    currentState = CoreRuntime.getRuntimeState(),
                    status = CoreRuntime.getRuntimeStatusSnapshot(),
                    message = "Action denied by runtime policy"
                )
            )

            auditProvider.record(
                RuntimeActionAuditRecord(
                    request = request,
                    success = false,
                    message = deniedResult.controlResult.message
                )
            )

            return deniedResult
        }

        val handler = registry.find {
            it.supports(request)
        }

        if (handler != null) {

            val result = handler.handle(request)

            auditProvider.record(
                RuntimeActionAuditRecord(
                    request = request,
                    success = result.success,
                    message = result.controlResult.message
                )
            )

            return result
        }

        val failureResult = RuntimeActionResult(
            request = request,
            success = false,
            controlResult = RuntimeControlResult(
                command = request.command,
                success = false,
                previousState = CoreRuntime.getRuntimeState(),
                currentState = CoreRuntime.getRuntimeState(),
                status = CoreRuntime.getRuntimeStatusSnapshot(),
                message = "No action handler for ${request.command}"
            )
        )

        auditProvider.record(
            RuntimeActionAuditRecord(
                request = request,
                success = false,
                message = failureResult.controlResult.message
            )
        )

        return failureResult
    }
}
