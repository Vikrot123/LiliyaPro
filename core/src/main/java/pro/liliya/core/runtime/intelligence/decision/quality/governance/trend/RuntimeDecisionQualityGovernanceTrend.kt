package pro.liliya.core.runtime.intelligence.decision.quality.governance.trend

import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState

data class RuntimeDecisionQualityGovernanceTrend(
    val state: RuntimeDecisionQualityGovernanceTrendState,
    val sampleCount: Int,
    val firstGovernanceState:
        RuntimeDecisionQualityGovernanceState?,
    val latestGovernanceState:
        RuntimeDecisionQualityGovernanceState?,
    val clearRatio: Double,
    val cautionRatio: Double,
    val reviewRatio: Double,
    val governanceTransitionCount: Int,
    val directionChanges: Int,
    val oscillating: Boolean
)
