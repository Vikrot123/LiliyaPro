package pro.liliya.core.runtime.intelligence.decision.reflection.trend

import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord

interface RuntimeDecisionReflectionTrendAnalyzer {

    fun analyze(
        records: List<RuntimeDecisionReflectionRecord>
    ): RuntimeDecisionReflectionTrend
}
