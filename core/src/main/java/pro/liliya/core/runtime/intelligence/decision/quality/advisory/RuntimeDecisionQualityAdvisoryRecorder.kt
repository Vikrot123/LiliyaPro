package pro.liliya.core.runtime.intelligence.decision.quality.advisory

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryRecord

interface RuntimeDecisionQualityAdvisoryRecorder {

    fun recordCurrent():
        RuntimeDecisionQualityAdvisoryRecord
}
