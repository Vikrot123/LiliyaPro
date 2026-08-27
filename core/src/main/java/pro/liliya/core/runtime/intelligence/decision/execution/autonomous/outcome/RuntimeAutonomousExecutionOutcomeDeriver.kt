package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.outcome

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline.RuntimeAutonomousExecutionPipelineResult

interface RuntimeAutonomousExecutionOutcomeDeriver {

    fun derive(
        result: RuntimeAutonomousExecutionPipelineResult
    ): RuntimeAutonomousExecutionOutcome
}
