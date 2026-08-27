package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult

interface RuntimeAutonomousExecutionEvaluationPipeline {

    fun evaluate(
        executionResult: RuntimeAutonomousExecutionPipelineResult
    ): RuntimeAutonomousExecutionEvaluationResult
}
