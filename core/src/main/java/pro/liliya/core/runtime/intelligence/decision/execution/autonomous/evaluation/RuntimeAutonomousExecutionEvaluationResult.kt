package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.assessment.RuntimeAutonomousExecutionAssessment
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome.RuntimeAutonomousExecutionOutcome
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult

data class RuntimeAutonomousExecutionEvaluationResult(
    val executionResult: RuntimeAutonomousExecutionPipelineResult,
    val outcome: RuntimeAutonomousExecutionOutcome,
    val assessment: RuntimeAutonomousExecutionAssessment
)
