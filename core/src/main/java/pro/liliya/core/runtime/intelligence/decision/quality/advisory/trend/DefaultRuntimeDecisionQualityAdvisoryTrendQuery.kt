package pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryHistory

class DefaultRuntimeDecisionQualityAdvisoryTrendQuery(
    private val history: RuntimeDecisionQualityAdvisoryHistory,
    private val analyzer:
        RuntimeDecisionQualityAdvisoryTrendAnalyzer =
        DefaultRuntimeDecisionQualityAdvisoryTrendAnalyzer()
) : RuntimeDecisionQualityAdvisoryTrendQuery {

    override fun currentTrend():
        RuntimeDecisionQualityAdvisoryTrend {

        return analyzer.analyze(
            history.records()
        )
    }
}
