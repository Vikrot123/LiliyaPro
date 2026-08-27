package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.DefaultRuntimeDecisionQualityAdvisorySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrend
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.DefaultRuntimeDecisionQualityGovernanceQuery
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendQuery

class DefaultRuntimeDecisionQualityGovernanceQueryContractTest {

    @Test
    fun insufficient_summary_maps_to_insufficient_governance() {
        val result =
            governance(
                summaryState =
                    RuntimeDecisionQualityAdvisorySummaryState
                        .INSUFFICIENT_DATA,
                level =
                    RuntimeDecisionQualityAdvisoryLevel.OBSERVE
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            result.state
        )

        assertFalse(result.proceedWithCaution)
        assertFalse(result.reviewRecommended)
    }

    @Test
    fun consistent_summary_maps_to_clear_governance() {
        val result =
            governance(
                summaryState =
                    RuntimeDecisionQualityAdvisorySummaryState
                        .CONSISTENT,
                level =
                    RuntimeDecisionQualityAdvisoryLevel.PROCEED
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.CLEAR,
            result.state
        )
    }

    @Test
    fun improving_summary_maps_to_clear_governance() {
        val result =
            governance(
                summaryState =
                    RuntimeDecisionQualityAdvisorySummaryState
                        .IMPROVING,
                level =
                    RuntimeDecisionQualityAdvisoryLevel.PROCEED
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.CLEAR,
            result.state
        )
    }

    @Test
    fun degrading_summary_maps_to_caution_not_review() {
        val result =
            governance(
                summaryState =
                    RuntimeDecisionQualityAdvisorySummaryState
                        .DEGRADING,
                level =
                    RuntimeDecisionQualityAdvisoryLevel.CAUTION
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.CAUTION,
            result.state
        )

        assertTrue(result.proceedWithCaution)
        assertFalse(result.reviewRecommended)
    }

    @Test
    fun volatile_summary_maps_to_caution() {
        val result =
            governance(
                summaryState =
                    RuntimeDecisionQualityAdvisorySummaryState
                        .VOLATILE,
                level =
                    RuntimeDecisionQualityAdvisoryLevel.CAUTION
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.CAUTION,
            result.state
        )
    }

    @Test
    fun explicit_review_has_priority_over_temporal_state() {
        val result =
            governance(
                summaryState =
                    RuntimeDecisionQualityAdvisorySummaryState
                        .CONSISTENT,
                level =
                    RuntimeDecisionQualityAdvisoryLevel.REVIEW
            )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            result.state
        )

        assertTrue(result.reviewRecommended)
        assertFalse(result.proceedWithCaution)
    }

    private fun governance(
        summaryState: RuntimeDecisionQualityAdvisorySummaryState,
        level: RuntimeDecisionQualityAdvisoryLevel
    ) =
        DefaultRuntimeDecisionQualityGovernanceQuery(
            summaryQuery =
                DefaultRuntimeDecisionQualityAdvisorySummaryQuery(
                    advisoryQuery =
                        object : RuntimeDecisionQualityAdvisoryQuery {
                            override fun currentAdvisory() =
                                advisory(level)
                        },
                    trendQuery =
                        object : RuntimeDecisionQualityAdvisoryTrendQuery {
                            override fun currentTrend() =
                                advisoryTrend(summaryState)
                        }
                )
        )
            .currentAssessment()

    private fun advisory(
        level: RuntimeDecisionQualityAdvisoryLevel
    ) =
        RuntimeDecisionQualityAdvisory(
            level = level,
            summary =
                RuntimeDecisionQualitySummary(
                    state =
                        RuntimeDecisionQualitySummaryState.HEALTHY,
                    assessment = null,
                    trend =
                        RuntimeDecisionQualityTrend(
                            state =
                                RuntimeDecisionQualityTrendState.STABLE,
                            sampleCount = 2,
                            firstQualityState = null,
                            latestQualityState = null,
                            trustworthyRatio = 1.0,
                            unstableRatio = 0.0,
                            attentionRatio = 0.0,
                            qualityTransitionCount = 0,
                            directionChanges = 0,
                            oscillating = false
                        ),
                    trustworthy = true,
                    requiresAttention = false,
                    temporallyStable = true,
                    oscillating = false,
                    reason =
                        "governance fixture"
                ),
            actionRecommended =
                level !=
                    RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            humanReviewRecommended =
                level ==
                    RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            reason =
                "governance fixture"
        )

    private fun advisoryTrend(
        state: RuntimeDecisionQualityAdvisorySummaryState
    ) =
        RuntimeDecisionQualityAdvisoryTrend(
            state =
                when (state) {
                    RuntimeDecisionQualityAdvisorySummaryState
                        .INSUFFICIENT_DATA ->
                        RuntimeDecisionQualityAdvisoryTrendState
                            .INSUFFICIENT_DATA

                    RuntimeDecisionQualityAdvisorySummaryState
                        .IMPROVING ->
                        RuntimeDecisionQualityAdvisoryTrendState
                            .IMPROVING

                    RuntimeDecisionQualityAdvisorySummaryState
                        .DEGRADING ->
                        RuntimeDecisionQualityAdvisoryTrendState
                            .DEGRADING

                    else ->
                        RuntimeDecisionQualityAdvisoryTrendState.STABLE
                },
            sampleCount =
                if (
                    state ==
                    RuntimeDecisionQualityAdvisorySummaryState
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
                if (
                    state ==
                    RuntimeDecisionQualityAdvisorySummaryState
                        .VOLATILE
                ) {
                    1
                } else {
                    0
                },
            oscillating =
                state ==
                    RuntimeDecisionQualityAdvisorySummaryState
                        .VOLATILE
        )
}
