package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.feedback.RuntimeAutonomousExecutionFeedback
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionAnalyzer

class DefaultRuntimeAutonomousExecutionReflectionAnalyzer(
    private val reflectionAnalyzer:
        RuntimeDecisionReflectionAnalyzer,
    private val evidenceDeriver:
        RuntimeAutonomousExecutionReflectionEvidenceDeriver =
        DefaultRuntimeAutonomousExecutionReflectionEvidenceDeriver(),
    private val explanationDeriver:
        RuntimeAutonomousExecutionReflectionExplanationDeriver =
        DefaultRuntimeAutonomousExecutionReflectionExplanationDeriver()
) : RuntimeAutonomousExecutionReflectionAnalyzer {

    override fun analyze(
        evaluation: RuntimeAutonomousExecutionEvaluationResult,
        feedback: RuntimeAutonomousExecutionFeedback
    ): RuntimeAutonomousExecutionReflectionAnalysisResult {

        val evidence =
            evidenceDeriver.derive(
                evaluation = evaluation,
                feedback = feedback
            )

        val explanation =
            explanationDeriver.derive(
                evidence
            )

        val insight =
            reflectionAnalyzer.analyze(
                explanation
            )

        return RuntimeAutonomousExecutionReflectionAnalysisResult(
            evidence = evidence,
            explanation = explanation,
            insight = insight
        )
    }
}
