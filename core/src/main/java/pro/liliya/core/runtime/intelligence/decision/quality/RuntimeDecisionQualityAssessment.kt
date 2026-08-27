package pro.liliya.core.runtime.intelligence.decision.quality

import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend

data class RuntimeDecisionQualityAssessment(
    val state: RuntimeDecisionQualityState,
    val reflection: RuntimeDecisionReflectionInsight,
    val trend: RuntimeDecisionReflectionTrend,
    val trustworthyKnowledgeBasis: Boolean,
    val requiresAttention: Boolean,
    val temporallyStable: Boolean,
    val confidence: Double,
    val reason: String
)
