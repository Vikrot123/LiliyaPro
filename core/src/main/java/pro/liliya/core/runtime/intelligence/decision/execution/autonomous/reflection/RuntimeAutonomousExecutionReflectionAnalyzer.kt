package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback

interface RuntimeAutonomousExecutionReflectionAnalyzer {

    fun analyze(
        evaluation: RuntimeAutonomousExecutionEvaluationResult,
        feedback: RuntimeAutonomousExecutionFeedback
    ): RuntimeAutonomousExecutionReflectionAnalysisResult
}
