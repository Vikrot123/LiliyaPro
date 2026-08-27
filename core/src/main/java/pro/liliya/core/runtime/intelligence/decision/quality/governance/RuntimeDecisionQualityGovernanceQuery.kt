package pro.liliya.core.runtime.intelligence.decision.quality.governance

interface RuntimeDecisionQualityGovernanceQuery {

    fun currentAssessment():
        RuntimeDecisionQualityGovernanceAssessment
}
