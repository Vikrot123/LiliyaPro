package pro.liliya.core.runtime.intelligence.decision.quality.governance

import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceHistory
import pro.liliya.core.runtime.intelligence.decision.quality.governance.history.RuntimeDecisionQualityGovernanceRecord

class DefaultRuntimeDecisionQualityGovernanceRecorder(
    private val query: RuntimeDecisionQualityGovernanceQuery,
    private val history: RuntimeDecisionQualityGovernanceHistory
) : RuntimeDecisionQualityGovernanceRecorder {

    override fun recordCurrent():
        RuntimeDecisionQualityGovernanceRecord {

        return history.record(
            query.currentAssessment()
        )
    }
}
