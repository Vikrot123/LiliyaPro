package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedbackState

data class RuntimeAutonomousExecutionReflectionEvidence(
    val evaluation: RuntimeAutonomousExecutionEvaluationResult,
    val feedback: RuntimeAutonomousExecutionFeedback,
    val decision: RuntimeDecision,
    val command: RuntimeCommand?,
    val decisionReason: String,
    val confidence: Double,
    val actionAttempted: Boolean,
    val actionSucceeded: Boolean?,
    val feedbackState: RuntimeAutonomousExecutionFeedbackState,
    val knowledgeUsed: Boolean,
    val consistent: Boolean
)
