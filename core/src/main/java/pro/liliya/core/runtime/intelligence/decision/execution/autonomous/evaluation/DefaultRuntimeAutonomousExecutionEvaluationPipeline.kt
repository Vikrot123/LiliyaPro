package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessmentDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcomeDeriver
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult

class DefaultRuntimeAutonomousExecutionEvaluationPipeline(
    private val outcomeDeriver:
        RuntimeAutonomousExecutionOutcomeDeriver,
    private val assessmentDeriver:
        RuntimeAutonomousExecutionAssessmentDeriver
) : RuntimeAutonomousExecutionEvaluationPipeline {

    override fun evaluate(
        executionResult: RuntimeAutonomousExecutionPipelineResult
    ): RuntimeAutonomousExecutionEvaluationResult {

        val outcome =
            outcomeDeriver.derive(
                executionResult
            )

        val assessment =
            assessmentDeriver.derive(
                outcome
            )

        return RuntimeAutonomousExecutionEvaluationResult(
            executionResult = executionResult,
            outcome = outcome,
            assessment = assessment
        )
    }
}
