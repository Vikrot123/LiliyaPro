package pro.liliya.core.runtime.intelligence.reasoning.orchestration

import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

interface RuntimeAutonomousReasoningPipeline {

    fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeAutonomousReasoningResult
}
