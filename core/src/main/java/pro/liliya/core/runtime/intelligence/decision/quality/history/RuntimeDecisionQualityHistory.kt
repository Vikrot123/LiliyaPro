package pro.liliya.core.runtime.intelligence.decision.quality.history

import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment

interface RuntimeDecisionQualityHistory {

    fun record(
        assessment: RuntimeDecisionQualityAssessment
    ): RuntimeDecisionQualityRecord

    fun records():
        List<RuntimeDecisionQualityRecord>

    fun clear()
}
