package pro.liliya.core.runtime.intelligence.decision.quality.governance.history

import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment

data class RuntimeDecisionQualityGovernanceRecord(
    val assessment: RuntimeDecisionQualityGovernanceAssessment,
    val recordedAt: Long
)
