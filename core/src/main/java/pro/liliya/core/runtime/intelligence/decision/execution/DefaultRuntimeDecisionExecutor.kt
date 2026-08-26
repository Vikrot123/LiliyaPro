package pro.liliya.core.runtime.intelligence.decision.execution

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionEngine
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

class DefaultRuntimeDecisionExecutor(
    private val decisionEngine: RuntimeDecisionEngine,
    private val requestFactory: RuntimeDecisionActionRequestFactory,
    private val actionDispatcher: RuntimeActionDispatcher
) : RuntimeDecisionExecutor {

    override fun execute(
        intelligence: RuntimeIntelligenceOrchestrationResult,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeDecisionExecutionResult {

        val decision = decisionEngine.decide(intelligence)

        val request = requestFactory.create(
            decision = decision,
            source = source,
            authority = authority
        )

        if (request == null) {
            return RuntimeDecisionExecutionResult(
                decision = decision,
                request = null,
                actionResult = null
            )
        }

        val actionResult = actionDispatcher.dispatch(request)

        return RuntimeDecisionExecutionResult(
            decision = decision,
            request = request,
            actionResult = actionResult
        )
    }
}
