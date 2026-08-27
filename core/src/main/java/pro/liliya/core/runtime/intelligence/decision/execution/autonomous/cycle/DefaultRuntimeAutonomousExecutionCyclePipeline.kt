package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.cycle

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.evaluation.RuntimeAutonomousExecutionEvaluationPipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipeline
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.postexecution.RuntimeAutonomousExecutionPostExecutionLearningPipeline
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

class DefaultRuntimeAutonomousExecutionCyclePipeline(
    private val executionPipeline:
        RuntimeAutonomousExecutionPipeline,
    private val evaluationPipeline:
        RuntimeAutonomousExecutionEvaluationPipeline,
    private val postExecutionLearningPipeline:
        RuntimeAutonomousExecutionPostExecutionLearningPipeline
) : RuntimeAutonomousExecutionCyclePipeline {

    override fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousExecutionCyclePipelineResult {

        val execution =
            executionPipeline.process(
                intelligence = intelligence,
                source = source,
                authority = authority
            )

        val evaluation =
            evaluationPipeline.evaluate(
                execution
            )

        val postExecution =
            postExecutionLearningPipeline.process(
                evaluation
            )

        return RuntimeAutonomousExecutionCyclePipelineResult(
            execution =
                execution,
            evaluation =
                evaluation,
            postExecution =
                postExecution
        )
    }
}
