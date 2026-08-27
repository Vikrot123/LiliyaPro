package pro.liliya.core.runtime.intelligence.decision.quality.advisory.history

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory

interface RuntimeDecisionQualityAdvisoryHistory {

    fun record(
        advisory: RuntimeDecisionQualityAdvisory
    ): RuntimeDecisionQualityAdvisoryRecord

    fun records():
        List<RuntimeDecisionQualityAdvisoryRecord>

    fun clear()
}
