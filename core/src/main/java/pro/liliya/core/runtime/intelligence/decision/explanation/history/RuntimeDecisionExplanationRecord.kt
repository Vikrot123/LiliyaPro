package pro.liliya.core.runtime.intelligence.decision.explanation.history

import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation

data class RuntimeDecisionExplanationRecord(
    val explanation: RuntimeDecisionExplanation,
    val recordedAt: Long
)
