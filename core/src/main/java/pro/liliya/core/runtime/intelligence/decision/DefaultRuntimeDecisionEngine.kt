package pro.liliya.core.runtime.intelligence.decision

import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

class DefaultRuntimeDecisionEngine : RuntimeDecisionEngine {

    override fun decide(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeDecision {
        return when (intelligence.meaning.significance) {
            RuntimeMeaningSignificance.STABLE ->
                RuntimeDecision(
                    command = null,
                    reason = "Runtime is stable; no action required",
                    confidence = intelligence.meaning.confidence
                )

            RuntimeMeaningSignificance.WARNING ->
                RuntimeDecision(
                    command = RuntimeCommand.HEALTH_CHECK,
                    reason = "Runtime shows signs of degradation; health check required",
                    confidence = intelligence.meaning.confidence
                )

            RuntimeMeaningSignificance.CRITICAL ->
                RuntimeDecision(
                    command = RuntimeCommand.RECOVER,
                    reason = "Runtime instability requires recovery",
                    confidence = intelligence.meaning.confidence
                )

            RuntimeMeaningSignificance.UNKNOWN ->
                RuntimeDecision(
                    command = RuntimeCommand.HEALTH_CHECK,
                    reason = "Runtime state is uncertain; health check required",
                    confidence = intelligence.meaning.confidence
                )
        }
    }
}
