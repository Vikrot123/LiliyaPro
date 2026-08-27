package pro.liliya.core.runtime.intelligence.decision.proposal

import pro.liliya.core.runtime.intelligence.cognition.RuntimeAutonomousCognitionResult

interface RuntimeAutonomousDecisionProposalDeriver {

    fun derive(
        cognition: RuntimeAutonomousCognitionResult
    ): RuntimeAutonomousDecisionProposal
}
