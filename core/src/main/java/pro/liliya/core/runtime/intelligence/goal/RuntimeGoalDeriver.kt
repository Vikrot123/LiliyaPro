package pro.liliya.core.runtime.intelligence.goal

import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

interface RuntimeGoalDeriver {

    fun derive(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeGoal
}
