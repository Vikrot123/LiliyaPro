package pro.liliya.core.runtime.intelligence.decision.quality.advisory

import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState

class DefaultRuntimeDecisionQualityAdvisoryQuery(
    private val summaryQuery: RuntimeDecisionQualitySummaryQuery
) : RuntimeDecisionQualityAdvisoryQuery {

    override fun currentAdvisory():
        RuntimeDecisionQualityAdvisory {

        val summary =
            summaryQuery.currentSummary()

        return when (summary.state) {
            RuntimeDecisionQualitySummaryState.INSUFFICIENT_DATA ->
                RuntimeDecisionQualityAdvisory(
                    level =
                        RuntimeDecisionQualityAdvisoryLevel.OBSERVE,
                    summary = summary,
                    actionRecommended = false,
                    humanReviewRecommended = false,
                    reason =
                        "Decision quality evidence is insufficient; continue observation"
                )

            RuntimeDecisionQualitySummaryState.HEALTHY ->
                RuntimeDecisionQualityAdvisory(
                    level =
                        RuntimeDecisionQualityAdvisoryLevel.PROCEED,
                    summary = summary,
                    actionRecommended = true,
                    humanReviewRecommended = false,
                    reason =
                        "Decision quality is healthy and supports proceeding"
                )

            RuntimeDecisionQualitySummaryState.WATCH ->
                RuntimeDecisionQualityAdvisory(
                    level =
                        RuntimeDecisionQualityAdvisoryLevel.CAUTION,
                    summary = summary,
                    actionRecommended = true,
                    humanReviewRecommended = false,
                    reason =
                        "Decision quality should be approached with caution"
                )

            RuntimeDecisionQualitySummaryState.ATTENTION ->
                RuntimeDecisionQualityAdvisory(
                    level =
                        RuntimeDecisionQualityAdvisoryLevel.REVIEW,
                    summary = summary,
                    actionRecommended = false,
                    humanReviewRecommended = true,
                    reason =
                        "Decision quality requires review before relying on it"
                )
        }
    }
}
