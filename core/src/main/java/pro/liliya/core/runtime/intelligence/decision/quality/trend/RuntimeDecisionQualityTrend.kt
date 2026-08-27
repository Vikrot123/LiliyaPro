package pro.liliya.core.runtime.intelligence.decision.quality.trend

import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState

data class RuntimeDecisionQualityTrend(
    val state: RuntimeDecisionQualityTrendState,
    val sampleCount: Int,
    val firstQualityState: RuntimeDecisionQualityState?,
    val latestQualityState: RuntimeDecisionQualityState?,
    val trustworthyRatio: Double,
    val unstableRatio: Double,
    val attentionRatio: Double,
    val qualityTransitionCount: Int,
    val directionChanges: Int,
    val oscillating: Boolean
)
