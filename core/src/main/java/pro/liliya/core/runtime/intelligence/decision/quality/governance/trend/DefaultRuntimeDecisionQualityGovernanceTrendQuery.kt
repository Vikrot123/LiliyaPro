package pro.liliya.core.runtime.intelligence.decision.quality.governance.trend

import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceHistory

class DefaultRuntimeDecisionQualityGovernanceTrendQuery(
    private val history:
        RuntimeDecisionQualityGovernanceHistory,
    private val analyzer:
        RuntimeDecisionQualityGovernanceTrendAnalyzer =
        DefaultRuntimeDecisionQualityGovernanceTrendAnalyzer()
) : RuntimeDecisionQualityGovernanceTrendQuery {

    override fun currentTrend():
        RuntimeDecisionQualityGovernanceTrend {

        return analyzer.analyze(
            history.records()
        )
    }
}
