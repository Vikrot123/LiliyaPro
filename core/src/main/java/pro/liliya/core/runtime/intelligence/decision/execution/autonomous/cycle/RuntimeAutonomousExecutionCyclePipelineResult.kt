package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.cycle

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution.RuntimeAutonomousExecutionPostExecutionLearningPipelineResult

data class RuntimeAutonomousExecutionCyclePipelineResult(
    val execution:
        RuntimeAutonomousExecutionPipelineResult,
    val evaluation:
        RuntimeAutonomousExecutionEvaluationResult,
    val postExecution:
        RuntimeAutonomousExecutionPostExecutionLearningPipelineResult
)
