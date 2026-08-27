package pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel

data class RuntimeDecisionQualityAdvisoryTrend(
    val state: RuntimeDecisionQualityAdvisoryTrendState,
    val sampleCount: Int,
    val firstLevel: RuntimeDecisionQualityAdvisoryLevel?,
    val latestLevel: RuntimeDecisionQualityAdvisoryLevel?,
    val proceedRatio: Double,
    val cautionRatio: Double,
    val reviewRatio: Double,
    val advisoryTransitionCount: Int,
    val directionChanges: Int,
    val oscillating: Boolean
)
