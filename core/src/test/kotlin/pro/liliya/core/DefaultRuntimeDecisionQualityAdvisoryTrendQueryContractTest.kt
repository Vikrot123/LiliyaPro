package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.DefaultRuntimeDecisionQualityAdvisoryHistory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.DefaultRuntimeDecisionQualityAdvisoryTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityAdvisoryTrendQueryContractTest {

    @Test
    fun query_reads_current_bounded_history_snapshot() {
        val history =
            DefaultRuntimeDecisionQualityAdvisoryHistory(
                capacity = 2
            )

        history.record(
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.REVIEW
            )
        )

        history.record(
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.CAUTION
            )
        )

        history.record(
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.PROCEED
            )
        )

        val trend =
            DefaultRuntimeDecisionQualityAdvisoryTrendQuery(
                history = history
            )
                .currentTrend()

        assertEquals(
            2,
            trend.sampleCount
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.CAUTION,
            trend.firstLevel
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.PROCEED,
            trend.latestLevel
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun clear_immediately_returns_insufficient_trend() {
        val history =
            DefaultRuntimeDecisionQualityAdvisoryHistory()

        history.record(
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.PROCEED
            )
        )

        history.record(
            advisory(
                RuntimeDecisionQualityAdvisoryLevel.PROCEED
            )
        )

        val query =
            DefaultRuntimeDecisionQualityAdvisoryTrendQuery(
                history = history
            )

        history.clear()

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState
                .INSUFFICIENT_DATA,
            query.currentTrend().state
        )
    }

    private fun advisory(
        level: RuntimeDecisionQualityAdvisoryLevel
    ) =
        RuntimeDecisionQualityAdvisory(
            level = level,
            summary =
                RuntimeDecisionQualitySummary(
                    state =
                        RuntimeDecisionQualitySummaryState
                            .INSUFFICIENT_DATA,
                    assessment = null,
                    trend =
                        RuntimeDecisionQualityTrend(
                            state =
                                RuntimeDecisionQualityTrendState
                                    .INSUFFICIENT_DATA,
                            sampleCount = 0,
                            firstQualityState = null,
                            latestQualityState = null,
                            trustworthyRatio = 0.0,
                            unstableRatio = 0.0,
                            attentionRatio = 0.0,
                            qualityTransitionCount = 0,
                            directionChanges = 0,
                            oscillating = false
                        ),
                    trustworthy = false,
                    requiresAttention = false,
                    temporallyStable = false,
                    oscillating = false,
                    reason =
                        "trend query fixture"
                ),
            actionRecommended =
                level ==
                    RuntimeDecisionQualityAdvisoryLevel.PROCEED ||
                    level ==
                    RuntimeDecisionQualityAdvisoryLevel.CAUTION,
            humanReviewRecommended =
                level ==
                    RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            reason =
                "trend query fixture"
        )
}
