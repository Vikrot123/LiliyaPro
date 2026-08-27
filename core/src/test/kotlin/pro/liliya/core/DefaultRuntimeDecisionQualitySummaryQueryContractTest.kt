package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityQuery
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.quality.summary.DefaultRuntimeDecisionQualitySummaryQuery
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendQuery
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionQualitySummaryQueryContractTest {

    @Test
    fun missing_current_assessment_is_insufficient_data() {
        val summary =
            query(
                assessment = null,
                trend =
                    qualityTrend(
                        RuntimeDecisionQualityTrendState
                            .INSUFFICIENT_DATA
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualitySummaryState
                .INSUFFICIENT_DATA,
            summary.state
        )

        assertNull(
            summary.assessment
        )
    }

    @Test
    fun trustworthy_stable_quality_is_healthy() {
        val summary =
            query(
                assessment =
                    assessment(
                        RuntimeDecisionQualityState.TRUSTWORTHY
                    ),
                trend =
                    qualityTrend(
                        RuntimeDecisionQualityTrendState.STABLE
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualitySummaryState.HEALTHY,
            summary.state
        )

        assertTrue(summary.trustworthy)
        assertTrue(summary.temporallyStable)
        assertFalse(summary.requiresAttention)
    }

    @Test
    fun degrading_trend_moves_trustworthy_quality_to_watch() {
        val summary =
            query(
                assessment =
                    assessment(
                        RuntimeDecisionQualityState.TRUSTWORTHY
                    ),
                trend =
                    qualityTrend(
                        RuntimeDecisionQualityTrendState.DEGRADING
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualitySummaryState.WATCH,
            summary.state
        )
    }

    @Test
    fun oscillation_moves_quality_to_watch_without_attention() {
        val summary =
            query(
                assessment =
                    assessment(
                        RuntimeDecisionQualityState.TRUSTWORTHY
                    ),
                trend =
                    qualityTrend(
                        RuntimeDecisionQualityTrendState.STABLE,
                        oscillating = true
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualitySummaryState.WATCH,
            summary.state
        )

        assertTrue(summary.oscillating)
        assertFalse(summary.requiresAttention)
    }

    @Test
    fun current_attention_has_priority_over_temporal_improvement() {
        val summary =
            query(
                assessment =
                    assessment(
                        RuntimeDecisionQualityState
                            .REQUIRES_ATTENTION
                    ),
                trend =
                    qualityTrend(
                        RuntimeDecisionQualityTrendState.IMPROVING
                    )
            )
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualitySummaryState.ATTENTION,
            summary.state
        )

        assertTrue(
            summary.requiresAttention
        )
    }

    private fun query(
        assessment: RuntimeDecisionQualityAssessment?,
        trend: RuntimeDecisionQualityTrend
    ) =
        DefaultRuntimeDecisionQualitySummaryQuery(
            qualityQuery =
                object : RuntimeDecisionQualityQuery {
                    override fun currentAssessment():
                        RuntimeDecisionQualityAssessment? =
                        assessment
                },
            trendQuery =
                object : RuntimeDecisionQualityTrendQuery {
                    override fun currentTrend():
                        RuntimeDecisionQualityTrend =
                        trend
                }
        )

    private fun assessment(
        state: RuntimeDecisionQualityState
    ) =
        RuntimeDecisionQualityAssessment(
            state = state,
            reflection =
                RuntimeDecisionReflectionInsight(
                    evidence =
                        RuntimeDecisionReflectionEvidence(
                            command = null,
                            decisionReason =
                                "quality summary",
                            confidence = 0.90,
                            knowledgeUsed =
                                state ==
                                    RuntimeDecisionQualityState
                                        .TRUSTWORTHY,
                            provenanceAvailable =
                                state ==
                                    RuntimeDecisionQualityState
                                        .TRUSTWORTHY,
                            provenanceValid =
                                if (
                                    state ==
                                    RuntimeDecisionQualityState
                                        .TRUSTWORTHY
                                ) {
                                    true
                                } else {
                                    null
                                },
                            provenanceDepth =
                                if (
                                    state ==
                                    RuntimeDecisionQualityState
                                        .TRUSTWORTHY
                                ) {
                                    1
                                } else {
                                    0
                                }
                        ),
                    trustworthyKnowledgeBasis =
                        state ==
                            RuntimeDecisionQualityState
                                .TRUSTWORTHY,
                    requiresAttention =
                        state ==
                            RuntimeDecisionQualityState
                                .REQUIRES_ATTENTION,
                    summary =
                        "quality summary"
                ),
            trend =
                RuntimeDecisionReflectionTrend(
                    state =
                        RuntimeDecisionReflectionTrendState.STABLE,
                    sampleCount = 2,
                    trustworthyRatio = 1.0,
                    attentionRatio = 0.0,
                    averageConfidence = 0.90,
                    firstConfidence = 0.90,
                    latestConfidence = 0.90,
                    confidenceDelta = 0.0
                ),
            trustworthyKnowledgeBasis =
                state ==
                    RuntimeDecisionQualityState.TRUSTWORTHY,
            requiresAttention =
                state ==
                    RuntimeDecisionQualityState
                        .REQUIRES_ATTENTION,
            temporallyStable = true,
            confidence = 0.90,
            reason =
                "quality summary"
        )

    private fun qualityTrend(
        state: RuntimeDecisionQualityTrendState,
        oscillating: Boolean = false
    ) =
        RuntimeDecisionQualityTrend(
            state = state,
            sampleCount =
                if (
                    state ==
                    RuntimeDecisionQualityTrendState
                        .INSUFFICIENT_DATA
                ) {
                    0
                } else {
                    2
                },
            firstQualityState =
                RuntimeDecisionQualityState.TRUSTWORTHY,
            latestQualityState =
                RuntimeDecisionQualityState.TRUSTWORTHY,
            trustworthyRatio = 1.0,
            unstableRatio = 0.0,
            attentionRatio = 0.0,
            qualityTransitionCount = 0,
            directionChanges =
                if (oscillating) 1 else 0,
            oscillating =
                oscillating
        )
}
