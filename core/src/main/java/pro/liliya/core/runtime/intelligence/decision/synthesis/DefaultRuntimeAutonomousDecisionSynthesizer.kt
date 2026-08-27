package pro.liliya.core.runtime.intelligence.decision.synthesis

import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState

class DefaultRuntimeAutonomousDecisionSynthesizer :
    RuntimeAutonomousDecisionSynthesizer {

    override fun synthesize(
        proposal: RuntimeAutonomousDecisionProposal
    ): RuntimeDecision {

        val command =
            when {
                !proposal.coherent ->
                    null

                !proposal.actionable ->
                    null

                proposal.state ==
                    RuntimeAutonomousDecisionProposalState.NO_ACTION ->
                    null

                proposal.state ==
                    RuntimeAutonomousDecisionProposalState.OBSERVE ->
                    null

                proposal.state ==
                    RuntimeAutonomousDecisionProposalState.INVESTIGATE ->
                    RuntimeCommand.HEALTH_CHECK

                proposal.state ==
                    RuntimeAutonomousDecisionProposalState.RECOVER ->
                    RuntimeCommand.RECOVER

                proposal.state ==
                    RuntimeAutonomousDecisionProposalState.WITHHOLD ->
                    null

                else ->
                    null
            }

        val reason =
            when {
                !proposal.coherent ->
                    "Autonomous decision withheld because proposal is incoherent"

                !proposal.actionable ->
                    "Autonomous decision withheld because proposal is not actionable"

                command == null ->
                    proposal.objective

                else ->
                    proposal.objective
            }

        return RuntimeDecision(
            command = command,
            reason = reason,
            confidence = proposal.confidence,
            knowledgeSelection = null
        )
    }
}
