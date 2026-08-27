package pro.liliya.core.runtime.intelligence.decision.quality.governance

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummary

data class RuntimeDecisionQualityGovernanceAssessment(
    val state: RuntimeDecisionQualityGovernanceState,
    val advisorySummary: RuntimeDecisionQualityAdvisorySummary,
    val proceedWithCaution: Boolean,
    val reviewRecommended: Boolean,
    val reason: String
)
