package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityAssessment
import pro.liliya.core.runtime.intelligence.decision.quality.RuntimeDecisionQualityState
import pro.liliya.core.runtime.intelligence.decision.quality.history.RuntimeDecisionQualityRecord
import pro.liliya.core.runtime.intelligence.decision.quality.trend.DefaultRuntimeDecisionQualityTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrend
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionQualityTrendAnalyzerContractTest {

    private val analyzer =
        DefaultRuntimeDecisionQualityTrendAnalyzer()

    @Test
    fun empty_history_is_insufficient_data() {
        val trend =
            analyzer.analyze(
                emptyList()
            )

        assertEquals(
            RuntimeDecisionQualityTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )

        assertEquals(
            0,
            trend.sampleCount
        )
    }

    @Test
    fun single_quality_record_is_insufficient_data() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityState.TRUSTWORTHY,
                        1L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityTrendState
                .INSUFFICIENT_DATA,
            trend.state
        )
    }

    @Test
    fun stable_trustworthy_quality_is_stable() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityState.TRUSTWORTHY,
                        1L
                    ),
                    record(
                        RuntimeDecisionQualityState.TRUSTWORTHY,
                        2L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityTrendState.STABLE,
            trend.state
        )

        assertEquals(
            1.0,
            trend.trustworthyRatio
        )

        assertFalse(
            trend.oscillating
        )
    }

    @Test
    fun attention_to_trustworthy_is_improving() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityState
                            .REQUIRES_ATTENTION,
                        1L
                    ),
                    record(
                        RuntimeDecisionQualityState.TRUSTWORTHY,
                        2L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun trustworthy_to_attention_is_degrading() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityState.TRUSTWORTHY,
                        1L
                    ),
                    record(
                        RuntimeDecisionQualityState
                            .REQUIRES_ATTENTION,
                        2L
                    )
                )
            )

        assertEquals(
            RuntimeDecisionQualityTrendState.DEGRADING,
            trend.state
        )
    }

    @Test
    fun quality_direction_reversal_is_detected_as_oscillation() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityState
                            .REQUIRES_ATTENTION,
                        1L
                    ),
                    record(
                        RuntimeDecisionQualityState.TRUSTWORTHY,
                        2L
                    ),
                    record(
                        RuntimeDecisionQualityState
                            .REQUIRES_ATTENTION,
                        3L
                    )
                )
            )

        assertTrue(
            trend.oscillating
        )

        assertEquals(
            1,
            trend.directionChanges
        )

        assertEquals(
            RuntimeDecisionQualityTrendState.STABLE,
            trend.state
        )
    }

    @Test
    fun transition_count_tracks_quality_state_changes() {
        val trend =
            analyzer.analyze(
                listOf(
                    record(
                        RuntimeDecisionQualityState.UNSTABLE,
                        1L
                    ),
                    record(
                        RuntimeDecisionQualityState.UNSTABLE,
                        2L
                    ),
                    record(
                        RuntimeDecisionQualityState.TRUSTWORTHY,
                        3L
                    )
                )
            )

        assertEquals(
            1,
            trend.qualityTransitionCount
        )
    }

    private fun record(
        state: RuntimeDecisionQualityState,
        recordedAt: Long
    ) =
        RuntimeDecisionQualityRecord(
            assessment =
                assessment(
                    state
                ),
            recordedAt =
                recordedAt
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
                                "quality trend",
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
                        "quality trend"
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
            temporallyStable =
                state !=
                    RuntimeDecisionQualityState.UNSTABLE,
            confidence = 0.90,
            reason =
                "quality trend"
        )
}
