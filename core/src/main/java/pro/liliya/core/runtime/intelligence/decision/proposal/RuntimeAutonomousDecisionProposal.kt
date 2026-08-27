package pro.liliya.core.runtime.intelligence.decision.proposal

import pro.liliya.core.runtime.intelligence.cognition.RuntimeAutonomousCognitionResult

data class RuntimeAutonomousDecisionProposal(
    val state: RuntimeAutonomousDecisionProposalState,
    val cognition: RuntimeAutonomousCognitionResult,
    val coherent: Boolean,
    val actionable: Boolean,
    val confidence: Double,
    val objective: String,
    val reason: String
)
