package pro.liliya.core.runtime.intelligence.decision

import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

interface RuntimeDecisionEngine {

    fun decide(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeDecision
}
