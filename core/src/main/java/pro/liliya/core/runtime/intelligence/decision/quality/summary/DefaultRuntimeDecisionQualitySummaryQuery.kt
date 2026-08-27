package pro.liliya.core.runtime.intelligence.decision.quality.summary

import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityQuery
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualitySummaryQuery(
    private val qualityQuery: RuntimeDecisionQualityQuery,
    private val trendQuery: RuntimeDecisionQualityTrendQuery
) : RuntimeDecisionQualitySummaryQuery {

    override fun currentSummary():
        RuntimeDecisionQualitySummary {

        val assessment =
            qualityQuery.currentAssessment()

        val trend =
            trendQuery.currentTrend()

        if (assessment == null) {
            return RuntimeDecisionQualitySummary(
                state =
                    RuntimeDecisionQualitySummaryState
                        .INSUFFICIENT_DATA,
                assessment = null,
                trend = trend,
                trustworthy = false,
                requiresAttention = false,
                temporallyStable = false,
                oscillating = trend.oscillating,
                reason =
                    "No current decision quality assessment"
            )
        }

        val state =
            classify(
                assessmentState = assessment.state,
                trendState = trend.state,
                oscillating = trend.oscillating
            )

        val reason =
            when (state) {
                RuntimeDecisionQualitySummaryState.ATTENTION ->
                    "Current decision quality requires attention"

                RuntimeDecisionQualitySummaryState.WATCH ->
                    when {
                        assessment.state ==
                            RuntimeDecisionQualityState.UNSTABLE ->
                            "Current decision quality is unstable"

                        trend.oscillating ->
                            "Decision quality is temporally oscillating"

                        trend.state ==
                            RuntimeDecisionQualityTrendState.DEGRADING ->
                            "Decision quality trend is degrading"

                        else ->
                            "Decision quality should be watched"
                    }

                RuntimeDecisionQualitySummaryState.HEALTHY ->
                    "Decision quality is trustworthy and temporally healthy"

                RuntimeDecisionQualitySummaryState.INSUFFICIENT_DATA ->
                    "Decision quality history is insufficient"
            }

        return RuntimeDecisionQualitySummary(
            state = state,
            assessment = assessment,
            trend = trend,
            trustworthy =
                assessment.state ==
                    RuntimeDecisionQualityState.TRUSTWORTHY,
            requiresAttention =
                state ==
                    RuntimeDecisionQualitySummaryState.ATTENTION,
            temporallyStable =
                trend.state ==
                    RuntimeDecisionQualityTrendState.STABLE &&
                    !trend.oscillating,
            oscillating =
                trend.oscillating,
            reason =
                reason
        )
    }

    private fun classify(
        assessmentState: RuntimeDecisionQualityState,
        trendState: RuntimeDecisionQualityTrendState,
        oscillating: Boolean
    ): RuntimeDecisionQualitySummaryState {

        if (
            assessmentState ==
            RuntimeDecisionQualityState.REQUIRES_ATTENTION
        ) {
            return RuntimeDecisionQualitySummaryState.ATTENTION
        }

        if (
            assessmentState ==
            RuntimeDecisionQualityState.INSUFFICIENT_DATA ||
            trendState ==
            RuntimeDecisionQualityTrendState.INSUFFICIENT_DATA
        ) {
            return RuntimeDecisionQualitySummaryState
                .INSUFFICIENT_DATA
        }

        if (
            assessmentState ==
            RuntimeDecisionQualityState.UNSTABLE ||
            trendState ==
            RuntimeDecisionQualityTrendState.DEGRADING ||
            oscillating
        ) {
            return RuntimeDecisionQualitySummaryState.WATCH
        }

        return RuntimeDecisionQualitySummaryState.HEALTHY
    }
}
