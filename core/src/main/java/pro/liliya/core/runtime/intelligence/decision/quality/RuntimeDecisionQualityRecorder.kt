package pro.liliya.core.runtime.intelligence.decision.quality

import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityRecord

interface RuntimeDecisionQualityRecorder {

    fun recordCurrent():
        RuntimeDecisionQualityRecord?
}
