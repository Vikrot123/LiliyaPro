package pro.liliya.core.runtime.intelligence.decision.quality.governance.trend

import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceRecord

interface RuntimeDecisionQualityGovernanceTrendAnalyzer {

    fun analyze(
        records:
            List<RuntimeDecisionQualityGovernanceRecord>
    ): RuntimeDecisionQualityGovernanceTrend
}
