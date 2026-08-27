package pro.liliya.core.runtime.intelligence.decision.synthesis

import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposal

interface RuntimeAutonomousDecisionSynthesizer {

    fun synthesize(
        proposal: RuntimeAutonomousDecisionProposal
    ): RuntimeDecision
}
