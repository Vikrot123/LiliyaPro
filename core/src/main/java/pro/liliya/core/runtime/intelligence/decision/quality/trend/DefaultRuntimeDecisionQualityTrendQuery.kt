package pro.liliya.core.runtime.intelligence.decision.quality.trend

import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityHistory

class DefaultRuntimeDecisionQualityTrendQuery(
    private val history: RuntimeDecisionQualityHistory,
    private val analyzer: RuntimeDecisionQualityTrendAnalyzer =
        DefaultRuntimeDecisionQualityTrendAnalyzer()
) : RuntimeDecisionQualityTrendQuery {

    override fun currentTrend():
        RuntimeDecisionQualityTrend {

        return analyzer.analyze(
            history.records()
        )
    }
}
