package pro.liliya.core.runtime.intelligence.decision.quality.advisory

import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary

data class RuntimeDecisionQualityAdvisory(
    val level: RuntimeDecisionQualityAdvisoryLevel,
    val summary: RuntimeDecisionQualitySummary,
    val actionRecommended: Boolean,
    val humanReviewRecommended: Boolean,
    val reason: String
)
