package pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrend

data class RuntimeDecisionQualityAdvisorySummary(
    val state: RuntimeDecisionQualityAdvisorySummaryState,
    val advisory: RuntimeDecisionQualityAdvisory,
    val trend: RuntimeDecisionQualityAdvisoryTrend,
    val stable: Boolean,
    val oscillating: Boolean,
    val requiresReview: Boolean,
    val reason: String
)
