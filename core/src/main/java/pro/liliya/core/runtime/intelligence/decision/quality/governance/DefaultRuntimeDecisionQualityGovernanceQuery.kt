package pro.liliya.core.runtime.intelligence.decision.quality.governance

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState

class DefaultRuntimeDecisionQualityGovernanceQuery(
    private val summaryQuery:
        RuntimeDecisionQualityAdvisorySummaryQuery
) : RuntimeDecisionQualityGovernanceQuery {

    override fun currentAssessment():
        RuntimeDecisionQualityGovernanceAssessment {

        val summary =
            summaryQuery.currentSummary()

        val state =
            when {
                summary.requiresReview ->
                    RuntimeDecisionQualityGovernanceState.REVIEW

                summary.state ==
                    RuntimeDecisionQualityAdvisorySummaryState
                        .INSUFFICIENT_DATA ->
                    RuntimeDecisionQualityGovernanceState
                        .INSUFFICIENT_DATA

                summary.state ==
                    RuntimeDecisionQualityAdvisorySummaryState
                        .DEGRADING ||
                    summary.state ==
                    RuntimeDecisionQualityAdvisorySummaryState
                        .VOLATILE ->
                    RuntimeDecisionQualityGovernanceState.CAUTION

                else ->
                    RuntimeDecisionQualityGovernanceState.CLEAR
            }

        val reason =
            when (state) {
                RuntimeDecisionQualityGovernanceState
                    .INSUFFICIENT_DATA ->
                    "Decision quality governance evidence is insufficient"

                RuntimeDecisionQualityGovernanceState.CLEAR ->
                    "Decision quality presents no governance concern"

                RuntimeDecisionQualityGovernanceState.CAUTION ->
                    "Decision quality warrants governance caution"

                RuntimeDecisionQualityGovernanceState.REVIEW ->
                    "Decision quality warrants governance review"
            }

        return RuntimeDecisionQualityGovernanceAssessment(
            state = state,
            advisorySummary = summary,
            proceedWithCaution =
                state ==
                    RuntimeDecisionQualityGovernanceState.CAUTION,
            reviewRecommended =
                state ==
                    RuntimeDecisionQualityGovernanceState.REVIEW,
            reason =
                reason
        )
    }
}
