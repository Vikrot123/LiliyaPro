package pro.liliya.core.runtime.intelligence.decision.quality.advisory.history

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory

data class RuntimeDecisionQualityAdvisoryRecord(
    val advisory: RuntimeDecisionQualityAdvisory,
    val recordedAt: Long
)
