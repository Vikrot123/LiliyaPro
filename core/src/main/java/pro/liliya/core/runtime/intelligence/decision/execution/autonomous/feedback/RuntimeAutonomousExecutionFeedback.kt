package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentState
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeState
import pro.liliya.core.runtime.intelligence.decision.proposal.RuntimeAutonomousDecisionProposalState

data class RuntimeAutonomousExecutionFeedback(
    val state: RuntimeAutonomousExecutionFeedbackState,
    val assessmentState: RuntimeAutonomousExecutionAssessmentState,
    val outcomeState: RuntimeAutonomousExecutionOutcomeState,
    val proposalState: RuntimeAutonomousDecisionProposalState,
    val command: RuntimeCommand?,
    val actionAttempted: Boolean,
    val actionSucceeded: Boolean?,
    val previousRuntimeState: CoreRuntimeState?,
    val currentRuntimeState: CoreRuntimeState?,
    val message: String?
)
