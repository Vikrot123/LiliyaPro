package pro.liliya.core.runtime.intelligence.decision.proposal

import pro.liliya.core.runtime.intelligence.cognition.RuntimeAutonomousCognitionResult
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyState

class DefaultRuntimeAutonomousDecisionProposalDeriver :
    RuntimeAutonomousDecisionProposalDeriver {

    override fun derive(
        cognition: RuntimeAutonomousCognitionResult
    ): RuntimeAutonomousDecisionProposal {

        if (!cognition.coherent) {
            return RuntimeAutonomousDecisionProposal(
                state =
                    RuntimeAutonomousDecisionProposalState.WITHHOLD,
                cognition = cognition,
                coherent = false,
                actionable = false,
                confidence = cognition.confidence,
                objective =
                    "Withhold autonomous decision proposal",
                reason =
                    "Autonomous cognition is not coherent"
            )
        }

        val state =
            when (cognition.strategy.state) {
                RuntimeStrategyState.MONITOR ->
                    RuntimeAutonomousDecisionProposalState.OBSERVE

                RuntimeStrategyState.PRESERVE ->
                    RuntimeAutonomousDecisionProposalState.NO_ACTION

                RuntimeStrategyState.DIAGNOSE ->
                    RuntimeAutonomousDecisionProposalState.INVESTIGATE

                RuntimeStrategyState.RESTORE ->
                    RuntimeAutonomousDecisionProposalState.RECOVER

                RuntimeStrategyState.DEFER ->
                    RuntimeAutonomousDecisionProposalState.WITHHOLD
            }

        val actionable =
            cognition.actionable &&
                state !=
                    RuntimeAutonomousDecisionProposalState.NO_ACTION &&
                state !=
                    RuntimeAutonomousDecisionProposalState.WITHHOLD

        val objective =
            when (state) {
                RuntimeAutonomousDecisionProposalState.NO_ACTION ->
                    "Preserve current runtime state without intervention"

                RuntimeAutonomousDecisionProposalState.OBSERVE ->
                    "Observe runtime state before intervention"

                RuntimeAutonomousDecisionProposalState.INVESTIGATE ->
                    "Investigate runtime degradation before intervention"

                RuntimeAutonomousDecisionProposalState.RECOVER ->
                    "Propose restoration of healthy runtime operation"

                RuntimeAutonomousDecisionProposalState.WITHHOLD ->
                    "Withhold autonomous decision proposal"
            }

        return RuntimeAutonomousDecisionProposal(
            state = state,
            cognition = cognition,
            coherent = true,
            actionable = actionable,
            confidence = cognition.confidence,
            objective = objective,
            reason =
                "Decision proposal derived from coherent autonomous cognition"
        )
    }
}
