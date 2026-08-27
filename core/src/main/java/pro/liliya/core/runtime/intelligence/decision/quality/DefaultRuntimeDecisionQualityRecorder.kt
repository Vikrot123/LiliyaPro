package pro.liliya.core.runtime.intelligence.decision.quality

import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityHistory
import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityRecord

class DefaultRuntimeDecisionQualityRecorder(
    private val query: RuntimeDecisionQualityQuery,
    private val history: RuntimeDecisionQualityHistory
) : RuntimeDecisionQualityRecorder {

    override fun recordCurrent():
        RuntimeDecisionQualityRecord? {

        val assessment =
            query.currentAssessment()
                ?: return null

        return history.record(
            assessment
        )
    }
}
