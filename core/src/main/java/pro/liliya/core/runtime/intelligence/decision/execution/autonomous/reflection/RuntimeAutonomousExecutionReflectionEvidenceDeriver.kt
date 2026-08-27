package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback

interface RuntimeAutonomousExecutionReflectionEvidenceDeriver {

    fun derive(
        evaluation: RuntimeAutonomousExecutionEvaluationResult,
        feedback: RuntimeAutonomousExecutionFeedback
    ): RuntimeAutonomousExecutionReflectionEvidence
}
