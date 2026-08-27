package pro.liliya.core.runtime.intelligence.decision.quality.governance.history

import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceAssessment

interface RuntimeDecisionQualityGovernanceHistory {

    fun record(
        assessment: RuntimeDecisionQualityGovernanceAssessment
    ): RuntimeDecisionQualityGovernanceRecord

    fun records():
        List<RuntimeDecisionQualityGovernanceRecord>

    fun clear()
}
