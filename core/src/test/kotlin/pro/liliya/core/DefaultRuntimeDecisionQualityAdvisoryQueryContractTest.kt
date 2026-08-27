package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.DefaultRuntimeDecisionQualityAdvisoryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityAdvisoryQueryContractTest {

    @Test
    fun insufficient_summary_maps_to_observe() {
        val advisory =
            query(
                summary(
                    RuntimeDecisionQualitySummaryState
                        .INSUFFICIENT_DATA
                )
            )
                .currentAdvisory()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.OBSERVE,
            advisory.level
        )

        assertFalse(advisory.actionRecommended)
        assertFalse(advisory.humanReviewRecommended)
    }

    @Test
    fun healthy_summary_maps_to_proceed() {
        val advisory =
            query(
                summary(
                    RuntimeDecisionQualitySummaryState.HEALTHY
                )
            )
                .currentAdvisory()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.PROCEED,
            advisory.level
        )

        assertTrue(advisory.actionRecommended)
        assertFalse(advisory.humanReviewRecommended)
    }

    @Test
    fun watch_summary_maps_to_caution() {
        val advisory =
            query(
                summary(
                    RuntimeDecisionQualitySummaryState.WATCH
                )
            )
                .currentAdvisory()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.CAUTION,
            advisory.level
        )

        assertTrue(advisory.actionRecommended)
        assertFalse(advisory.humanReviewRecommended)
    }

    @Test
    fun attention_summary_maps_to_review_without_policy_denial_semantics() {
        val advisory =
            query(
                summary(
                    RuntimeDecisionQualitySummaryState.ATTENTION
                )
            )
                .currentAdvisory()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            advisory.level
        )

        assertFalse(advisory.actionRecommended)
        assertTrue(advisory.humanReviewRecommended)
    }

    private fun query(
        summary: RuntimeDecisionQualitySummary
    ) =
        DefaultRuntimeDecisionQualityAdvisoryQuery(
            summaryQuery =
                object : RuntimeDecisionQualitySummaryQuery {
                    override fun currentSummary():
                        RuntimeDecisionQualitySummary =
                        summary
                }
        )

    private fun summary(
        state: RuntimeDecisionQualitySummaryState
    ) =
        RuntimeDecisionQualitySummary(
            state = state,
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
            trustworthy =
                state ==
                    RuntimeDecisionQualitySummaryState.HEALTHY,
            requiresAttention =
                state ==
                    RuntimeDecisionQualitySummaryState.ATTENTION,
            temporallyStable =
                state ==
                    RuntimeDecisionQualitySummaryState.HEALTHY,
            oscillating = false,
            reason =
                "advisory fixture"
        )
}
