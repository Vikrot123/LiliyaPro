package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult

interface RuntimeAutonomousExecutionPostExecutionLearningPipeline {

    fun process(
        evaluation: RuntimeAutonomousExecutionEvaluationResult
    ): RuntimeAutonomousExecutionPostExecutionLearningPipelineResult
}
