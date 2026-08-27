package pro.liliya.core.runtime.intelligence.cognition

import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

interface RuntimeAutonomousCognitionPipeline {

    fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeAutonomousCognitionResult
}
