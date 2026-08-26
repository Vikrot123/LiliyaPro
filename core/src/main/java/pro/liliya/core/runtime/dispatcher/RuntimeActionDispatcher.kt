package pro.liliya.core.runtime.dispatcher

import pro.liliya.core.runtime.composition.RuntimeComposition
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
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
    private val policyEvaluator: RuntimeActionPolicyEvaluator,
    private val runtimeComposition: RuntimeComposition
) {

    fun dispatch(
        request: RuntimeActionRequest
    ): RuntimeActionResult {

        val policyResult = policyEvaluator.evaluate(request)

        if (policyResult.decision == RuntimeActionPolicyDecision.DENY) {

            val deniedResult = RuntimeActionResult(
                request = request,
                success = false,
                controlResult = RuntimeControlResult(
                    command = request.command,
                    success = false,
                    previousState = runtimeComposition.runtimeState(),
                    currentState = runtimeComposition.runtimeState(),
                    status = createStatus(),
                    message = policyResult.reason
                )
            )

            auditProvider.record(
                RuntimeActionAuditRecord(
                    request = request,
                    success = false,
                    message = deniedResult.controlResult.message,
                    policyId = policyResult.policyId,
                    policyDecision = policyResult.decision,
                            authoritySource = policyResult.authoritySource,
                            authorityLevel = policyResult.authorityLevel,
                    capabilityAllowed = policyResult.capabilityAllowed,
                    capabilityDescription = policyResult.capabilityDescription,
                    requiredAuthority = policyResult.requiredAuthority,
                    actualAuthority = policyResult.actualAuthority
                )
            )

            recordCommandHistory(deniedResult)


            return deniedResult
        }

        val handler = registry.find {
            it.supports(request)
        }

        if (handler != null) {

            val result = try {
                handler.handle(request)
            } catch (e: Exception) {

                val failureResult = RuntimeActionResult(
                    request = request,
                    success = false,
                    controlResult = RuntimeControlResult(
                        command = request.command,
                        success = false,
                        previousState = runtimeComposition.runtimeState(),
                        currentState = runtimeComposition.runtimeState(),
                        status = createStatus(),
                        message = e.message ?: "Action handler failed"
                    )
                )

                auditProvider.record(
                    RuntimeActionAuditRecord(
                        request = request,
                        success = false,
                        message = failureResult.controlResult.message,
                        policyId = policyResult.policyId,
                        policyDecision = policyResult.decision,
                        authoritySource = policyResult.authoritySource,
                        authorityLevel = policyResult.authorityLevel,
                        capabilityAllowed = policyResult.capabilityAllowed,
                        capabilityDescription = policyResult.capabilityDescription,
                        requiredAuthority = policyResult.requiredAuthority,
                        actualAuthority = policyResult.actualAuthority
                    )
                )

                recordCommandHistory(failureResult)
                return failureResult
            }

            recordCommandHistory(result)

            auditProvider.record(
                RuntimeActionAuditRecord(
                    request = request,
                    success = result.success,
                    message = result.controlResult.message,
                    policyId = policyResult.policyId,
                    policyDecision = policyResult.decision,
                            authoritySource = policyResult.authoritySource,
                            authorityLevel = policyResult.authorityLevel,
                    capabilityAllowed = policyResult.capabilityAllowed,
                    capabilityDescription = policyResult.capabilityDescription,
                    requiredAuthority = policyResult.requiredAuthority,
                    actualAuthority = policyResult.actualAuthority
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
                previousState = runtimeComposition.runtimeState(),
                currentState = runtimeComposition.runtimeState(),
                status = createStatus(),
                message = "No action handler for ${request.command}"
            )
        )

        auditProvider.record(
            RuntimeActionAuditRecord(
                request = request,
                success = false,
                message = failureResult.controlResult.message,
policyId = policyResult.policyId,
policyDecision = policyResult.decision,
authoritySource = policyResult.authoritySource,
authorityLevel = policyResult.authorityLevel,
                    capabilityAllowed = policyResult.capabilityAllowed,
                    capabilityDescription = policyResult.capabilityDescription,
                    requiredAuthority = policyResult.requiredAuthority,
                    actualAuthority = policyResult.actualAuthority
            )
        )

        recordCommandHistory(failureResult)
        return failureResult
    }

    private fun recordCommandHistory(
        result: RuntimeActionResult
    ) {
        runtimeComposition.commandHistoryProvider().record(
            pro.liliya.core.runtime.history.RuntimeCommandRecord(
                command = result.controlResult.command,
                success = result.success,
                previousState = result.controlResult.previousState,
                currentState = result.controlResult.currentState,
                message = result.controlResult.message
            )
        )
    }

    private fun createStatus(): RuntimeStatusSnapshot {
        return runtimeComposition.runtimeStatusSnapshot()
    }
}
