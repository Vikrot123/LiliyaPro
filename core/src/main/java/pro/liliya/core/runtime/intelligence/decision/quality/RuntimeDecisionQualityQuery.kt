package pro.liliya.core.runtime.intelligence.decision.quality

interface RuntimeDecisionQualityQuery {

    fun currentAssessment():
        RuntimeDecisionQualityAssessment?
}
