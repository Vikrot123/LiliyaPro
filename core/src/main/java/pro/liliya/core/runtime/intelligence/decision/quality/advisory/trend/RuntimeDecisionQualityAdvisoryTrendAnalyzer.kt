package pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryRecord

interface RuntimeDecisionQualityAdvisoryTrendAnalyzer {

    fun analyze(
        records: List<RuntimeDecisionQualityAdvisoryRecord>
    ): RuntimeDecisionQualityAdvisoryTrend
}
