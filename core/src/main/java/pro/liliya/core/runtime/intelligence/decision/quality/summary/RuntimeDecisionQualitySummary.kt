package pro.liliya.core.runtime.intelligence.decision.quality.summary

import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend

data class RuntimeDecisionQualitySummary(
    val state: RuntimeDecisionQualitySummaryState,
    val assessment: RuntimeDecisionQualityAssessment?,
    val trend: RuntimeDecisionQualityTrend,
    val trustworthy: Boolean,
    val requiresAttention: Boolean,
    val temporallyStable: Boolean,
    val oscillating: Boolean,
    val reason: String
)
