package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState

data class RuntimeAutonomousExecutionAssessment(
    val state: RuntimeAutonomousExecutionAssessmentState,
    val outcomeState: RuntimeAutonomousExecutionOutcomeState,
    val proposalState: RuntimeAutonomousDecisionProposalState,
    val command: RuntimeCommand?,
    val actionAttempted: Boolean,
    val actionSucceeded: Boolean?,
    val previousRuntimeState: CoreRuntimeState?,
    val currentRuntimeState: CoreRuntimeState?,
    val message: String?
)
