package pro.liliya.core.runtime.intelligence.decision.quality.trend

import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityRecord

interface RuntimeDecisionQualityTrendAnalyzer {

    fun analyze(
        records: List<RuntimeDecisionQualityRecord>
    ): RuntimeDecisionQualityTrend
}
