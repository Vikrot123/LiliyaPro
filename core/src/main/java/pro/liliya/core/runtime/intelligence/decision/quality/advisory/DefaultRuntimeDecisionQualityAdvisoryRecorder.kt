package pro.liliya.core.runtime.intelligence.decision.quality.advisory

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryHistory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryRecord

class DefaultRuntimeDecisionQualityAdvisoryRecorder(
    private val query: RuntimeDecisionQualityAdvisoryQuery,
    private val history: RuntimeDecisionQualityAdvisoryHistory
) : RuntimeDecisionQualityAdvisoryRecorder {

    override fun recordCurrent():
        RuntimeDecisionQualityAdvisoryRecord {

        return history.record(
            query.currentAdvisory()
        )
    }
}
