package pro.liliya.core.runtime.intelligence.decision.quality.history

import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment

data class RuntimeDecisionQualityRecord(
    val assessment: RuntimeDecisionQualityAssessment,
    val recordedAt: Long
)
