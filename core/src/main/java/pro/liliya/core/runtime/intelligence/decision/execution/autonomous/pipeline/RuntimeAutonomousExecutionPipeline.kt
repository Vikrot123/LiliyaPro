package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.pipeline

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

interface RuntimeAutonomousExecutionPipeline {

    fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousExecutionPipelineResult
}
