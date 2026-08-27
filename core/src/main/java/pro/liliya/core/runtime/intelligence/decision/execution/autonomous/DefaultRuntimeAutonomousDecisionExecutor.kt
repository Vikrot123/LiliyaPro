package pro.liliya.core.runtime.intelligence.decision.execution.autonomous

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal
import pro.liliya.core.runtime.intelligence.decision.synthesis.RuntimeAutonomousDecisionSynthesizer

class DefaultRuntimeAutonomousDecisionExecutor(
    private val synthesizer:
        RuntimeAutonomousDecisionSynthesizer,
    private val requestFactory:
        RuntimeDecisionActionRequestFactory,
    private val actionDispatcher:
        RuntimeActionDispatcher
) : RuntimeAutonomousDecisionExecutor {

    override fun execute(
        proposal: RuntimeAutonomousDecisionProposal,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousDecisionExecutionResult {

        val decision =
            synthesizer.synthesize(
                proposal
            )

        val request =
            requestFactory.create(
                decision = decision,
                source = source,
                authority = authority
            )

        if (request == null) {
            return RuntimeAutonomousDecisionExecutionResult(
                proposal = proposal,
                decision = decision,
                request = null,
                actionResult = null
            )
        }

        val actionResult =
            actionDispatcher.dispatch(
                request
            )

        return RuntimeAutonomousDecisionExecutionResult(
            proposal = proposal,
            decision = decision,
            request = request,
            actionResult = actionResult
        )
    }
}
