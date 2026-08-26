package pro.liliya.core.runtime.intelligence.decision.execution

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

interface RuntimeDecisionExecutor {

    fun execute(
        intelligence: RuntimeIntelligenceOrchestrationResult,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeDecisionExecutionResult
}
