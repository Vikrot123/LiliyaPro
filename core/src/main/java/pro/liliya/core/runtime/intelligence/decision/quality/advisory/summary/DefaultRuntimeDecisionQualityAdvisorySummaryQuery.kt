package pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary

import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState

class DefaultRuntimeDecisionQualityAdvisorySummaryQuery(
    private val advisoryQuery:
        RuntimeDecisionQualityAdvisoryQuery,
    private val trendQuery:
        RuntimeDecisionQualityAdvisoryTrendQuery
) : RuntimeDecisionQualityAdvisorySummaryQuery {

    override fun currentSummary():
        RuntimeDecisionQualityAdvisorySummary {

        val advisory =
            advisoryQuery.currentAdvisory()

        val trend =
            trendQuery.currentTrend()

        val state =
            classify(
                trendState = trend.state,
                oscillating = trend.oscillating
            )

        val reason =
            when (state) {
                RuntimeDecisionQualityAdvisorySummaryState
                    .INSUFFICIENT_DATA ->
                    "Advisory history is insufficient"

                RuntimeDecisionQualityAdvisorySummaryState
                    .CONSISTENT ->
                    "Decision quality advisory is temporally consistent"

                RuntimeDecisionQualityAdvisorySummaryState
                    .IMPROVING ->
                    "Decision quality advisory is improving over time"

                RuntimeDecisionQualityAdvisorySummaryState
                    .DEGRADING ->
                    "Decision quality advisory is degrading over time"

                RuntimeDecisionQualityAdvisorySummaryState
                    .VOLATILE ->
                    "Decision quality advisory is temporally volatile"
            }

        return RuntimeDecisionQualityAdvisorySummary(
            state = state,
            advisory = advisory,
            trend = trend,
            stable =
                state ==
                    RuntimeDecisionQualityAdvisorySummaryState
                        .CONSISTENT,
            oscillating =
                trend.oscillating,
            requiresReview =
                advisory.level ==
                    RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            reason =
                reason
        )
    }

    private fun classify(
        trendState: RuntimeDecisionQualityAdvisoryTrendState,
        oscillating: Boolean
    ): RuntimeDecisionQualityAdvisorySummaryState {

        if (oscillating) {
            return RuntimeDecisionQualityAdvisorySummaryState
                .VOLATILE
        }

        return when (trendState) {
            RuntimeDecisionQualityAdvisoryTrendState
                .INSUFFICIENT_DATA ->
                RuntimeDecisionQualityAdvisorySummaryState
                    .INSUFFICIENT_DATA

            RuntimeDecisionQualityAdvisoryTrendState.STABLE ->
                RuntimeDecisionQualityAdvisorySummaryState
                    .CONSISTENT

            RuntimeDecisionQualityAdvisoryTrendState.IMPROVING ->
                RuntimeDecisionQualityAdvisorySummaryState
                    .IMPROVING

            RuntimeDecisionQualityAdvisoryTrendState.DEGRADING ->
                RuntimeDecisionQualityAdvisorySummaryState
                    .DEGRADING
        }
    }
}
