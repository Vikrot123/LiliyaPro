package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisory
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.history.RuntimeDecisionQualityAdvisoryRecord
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.DefaultRuntimeDecisionQualityAdvisoryTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummary
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrend
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState

class DefaultRuntimeDecisionQualityAdvisoryTrendAnalyzerContractTest {

    private val analyzer =
        DefaultRuntimeDecisionQualityAdvisoryTrendAnalyzer()

    @Test
    fun empty_history_is_insufficient() {
        val trend =
            analyzer.analyze(
                emptyList()
            )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )

        assertEquals(
            0,
            trend.sampleCount
        )
    }

    @Test
    fun one_record_is_insufficient() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityAdvisoryLevel
                            .PROCEED
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )
    }

    @Test
    fun review_to_proceed_is_improving() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.REVIEW
                    ),
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.PROCEED
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun proceed_to_review_is_degrading() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.PROCEED
                    ),
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.REVIEW
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState.DEGRADING,
            trend.state
        )
    }

    @Test
    fun repeated_same_advisory_is_stable() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.CAUTION
                    ),
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.CAUTION
                    ),
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.CAUTION
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState.STABLE,
            trend.state
        )

        assertFalse(
            trend.oscillating
        )

        assertEquals(
            0,
            trend.advisoryTransitionCount
        )
    }

    @Test
    fun alternating_advisory_is_detected_as_oscillating() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.PROCEED
                    ),
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.REVIEW
                    ),
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.PROCEED
                    ),
                    record(
                        RuntimeDecisionQualityAdvisoryLevel.REVIEW
                    )
                )
            )

        assertTrue(
            trend.oscillating
        )

        assertTrue(
            trend.directionChanges > 0
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState.STABLE,
            trend.state
        )
    }

    private fun record(
        level: RuntimeDecisionQualityAdvisoryLevel
    ) =
        RuntimeDecisionQualityAdvisoryRecord(
            advisory =
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
                                "advisory trend fixture"
                        ),
                    actionRecommended =
                        level ==
                            RuntimeDecisionQualityAdvisoryLevel
                                .PROCEED ||
                            level ==
                            RuntimeDecisionQualityAdvisoryLevel
                                .CAUTION,
                    humanReviewRecommended =
                        level ==
                            RuntimeDecisionQualityAdvisoryLevel
                                .REVIEW,
                    reason =
                        "advisory trend fixture"
                ),
            recordedAt = 1L
        )
}
