package pro.liliya.core.runtime.intelligence.decision.quality.governance

import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceRecord

interface RuntimeDecisionQualityGovernanceRecorder {

    fun recordCurrent():
        RuntimeDecisionQualityGovernanceRecord
}
