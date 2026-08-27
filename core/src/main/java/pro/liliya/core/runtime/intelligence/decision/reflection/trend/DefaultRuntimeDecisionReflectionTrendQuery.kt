package pro.liliya.core.runtime.intelligence.decision.reflection.trend

import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionHistory

class DefaultRuntimeDecisionReflectionTrendQuery(
    private val history: RuntimeDecisionReflectionHistory,
    private val analyzer: RuntimeDecisionReflectionTrendAnalyzer =
        DefaultRuntimeDecisionReflectionTrendAnalyzer()
) : RuntimeDecisionReflectionTrendQuery {

    override fun currentTrend():
        RuntimeDecisionReflectionTrend {

        return analyzer.analyze(
            history.records()
        )
    }
}
