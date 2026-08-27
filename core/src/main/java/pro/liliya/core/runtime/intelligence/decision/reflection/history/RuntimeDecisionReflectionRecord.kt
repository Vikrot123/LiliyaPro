package pro.liliya.core.runtime.intelligence.decision.reflection.history

import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight

data class RuntimeDecisionReflectionRecord(
    val insight: RuntimeDecisionReflectionInsight,
    val recordedAt: Long
)
