package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.DefaultRuntimeDecisionQualityAdvisorySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrend
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityAdvisorySummaryQueryContractTest {

    @Test
    fun insufficient_advisory_trend_is_insufficient_summary() {
        val summary =
            query(
                advisory =
                    advisory(
                        RuntimeDecisionQualityAdvisoryLevel.OBSERVE
                    ),
                trend =
                    advisoryTrend(
                        RuntimeDecisionQualityAdvisoryTrendState
                            .INSUFFICIENT_DATA
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState
                .INSUFFICIENT_DATA,
            summary.state
        )

        assertFalse(summary.stable)
        assertFalse(summary.oscillating)
    }

    @Test
    fun stable_advisory_trend_is_consistent() {
        val summary =
            query(
                advisory =
                    advisory(
                        RuntimeDecisionQualityAdvisoryLevel.PROCEED
                    ),
                trend =
                    advisoryTrend(
                        RuntimeDecisionQualityAdvisoryTrendState.STABLE
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState.CONSISTENT,
            summary.state
        )

        assertTrue(summary.stable)
        assertFalse(summary.requiresReview)
    }

    @Test
    fun improving_advisory_trend_is_improving_summary() {
        val summary =
            query(
                advisory =
                    advisory(
                        RuntimeDecisionQualityAdvisoryLevel.PROCEED
                    ),
                trend =
                    advisoryTrend(
                        RuntimeDecisionQualityAdvisoryTrendState
                            .IMPROVING
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState.IMPROVING,
            summary.state
        )
    }

    @Test
    fun degrading_advisory_trend_is_degrading_summary() {
        val summary =
            query(
                advisory =
                    advisory(
                        RuntimeDecisionQualityAdvisoryLevel.REVIEW
                    ),
                trend =
                    advisoryTrend(
                        RuntimeDecisionQualityAdvisoryTrendState
                            .DEGRADING
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState.DEGRADING,
            summary.state
        )

        assertTrue(summary.requiresReview)
    }

    @Test
    fun oscillation_has_priority_over_stable_trend_state() {
        val summary =
            query(
                advisory =
                    advisory(
                        RuntimeDecisionQualityAdvisoryLevel.CAUTION
                    ),
                trend =
                    advisoryTrend(
                        RuntimeDecisionQualityAdvisoryTrendState.STABLE,
                        oscillating = true
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState.VOLATILE,
            summary.state
        )

        assertTrue(summary.oscillating)
        assertFalse(summary.stable)
    }

    private fun query(
        advisory: RuntimeDecisionQualityAdvisory,
        trend: RuntimeDecisionQualityAdvisoryTrend
    ) =
        DefaultRuntimeDecisionQualityAdvisorySummaryQuery(
            advisoryQuery =
                object : RuntimeDecisionQualityAdvisoryQuery {
                    override fun currentAdvisory():
                        RuntimeDecisionQualityAdvisory =
                        advisory
                },
            trendQuery =
                object : RuntimeDecisionQualityAdvisoryTrendQuery {
                    override fun currentTrend():
                        RuntimeDecisionQualityAdvisoryTrend =
                        trend
                }
        )

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
                        "advisory summary fixture"
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
                "advisory summary fixture"
        )

    private fun advisoryTrend(
        state: RuntimeDecisionQualityAdvisoryTrendState,
        oscillating: Boolean = false
    ) =
        RuntimeDecisionQualityAdvisoryTrend(
            state = state,
            sampleCount =
                if (
                    state ==
                    RuntimeDecisionQualityAdvisoryTrendState
                        .INSUFFICIENT_DATA
                ) {
                    0
                } else {
                    2
                },
            firstLevel =
                RuntimeDecisionQualityAdvisoryLevel.OBSERVE,
            latestLevel =
                RuntimeDecisionQualityAdvisoryLevel.PROCEED,
            proceedRatio = 0.5,
            cautionRatio = 0.0,
            reviewRatio = 0.0,
            advisoryTransitionCount = 1,
            directionChanges =
                if (oscillating) {
                    1
                } else {
                    0
                },
            oscillating =
                oscillating
        )
}
