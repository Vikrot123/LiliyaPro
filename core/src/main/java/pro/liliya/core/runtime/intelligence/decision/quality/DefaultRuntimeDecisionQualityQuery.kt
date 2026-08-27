package pro.liliya.core.runtime.intelligence.decision.quality

import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendQuery

class DefaultRuntimeDecisionQualityQuery(
    private val reflectionHistory:
        RuntimeDecisionReflectionHistory,
    private val trendQuery:
        RuntimeDecisionReflectionTrendQuery,
    private val assessor:
        RuntimeDecisionQualityAssessor =
        DefaultRuntimeDecisionQualityAssessor()
) : RuntimeDecisionQualityQuery {

    override fun currentAssessment():
        RuntimeDecisionQualityAssessment? {

        val latest =
            reflectionHistory
                .records()
                .lastOrNull()
                ?.insight
                ?: return null

        return assessor.assess(
            reflection = latest,
            trend = trendQuery.currentTrend()
        )
    }
}
