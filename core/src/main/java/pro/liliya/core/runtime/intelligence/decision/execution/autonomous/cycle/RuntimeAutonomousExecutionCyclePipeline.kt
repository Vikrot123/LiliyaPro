package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.cycle

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

interface RuntimeAutonomousExecutionCyclePipeline {

    fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousExecutionCyclePipelineResult
}
