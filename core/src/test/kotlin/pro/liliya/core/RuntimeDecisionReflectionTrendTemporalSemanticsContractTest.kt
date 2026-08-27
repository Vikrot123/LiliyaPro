package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.history.RuntimeDecisionReflectionRecord
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.DefaultRuntimeDecisionReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class RuntimeDecisionReflectionTrendTemporalSemanticsContractTest {

    private val analyzer =
        DefaultRuntimeDecisionReflectionTrendAnalyzer()

    @Test
    fun good_bad_good_is_stable_but_explicitly_oscillating() {
        val trend =
            analyzer.analyze(
                listOf(
                    good(0.90, 1L),
                    bad(0.60, 2L),
                    good(0.90, 3L)
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.STABLE,
            trend.state
        )

        assertEquals(
            2,
            trend.qualityTransitionCount
        )

        assertTrue(
            trend.oscillating
        )
    }

    @Test
    fun bad_good_bad_is_degrading_and_oscillating() {
        val trend =
            analyzer.analyze(
                listOf(
                    bad(0.60, 1L),
                    good(0.90, 2L),
                    bad(0.60, 3L)
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.DEGRADING,
            trend.state
        )

        assertEquals(
            2,
            trend.qualityTransitionCount
        )

        assertTrue(
            trend.oscillating
        )
    }

    @Test
    fun sustained_good_sequence_is_stable_and_not_oscillating() {
        val trend =
            analyzer.analyze(
                listOf(
                    good(0.90, 1L),
                    good(0.91, 2L),
                    good(0.92, 3L)
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.STABLE,
            trend.state
        )

        assertEquals(
            0,
            trend.qualityTransitionCount
        )

        assertEquals(
            0,
            trend.confidenceDirectionChanges
        )

        assertFalse(
            trend.oscillating
        )
    }

    @Test
    fun sustained_bad_sequence_is_degrading_without_oscillation() {
        val trend =
            analyzer.analyze(
                listOf(
                    bad(0.60, 1L),
                    bad(0.59, 2L),
                    bad(0.58, 3L)
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.DEGRADING,
            trend.state
        )

        assertEquals(
            0,
            trend.qualityTransitionCount
        )

        assertFalse(
            trend.oscillating
        )
    }

    @Test
    fun confidence_up_then_down_is_detected_as_oscillation_even_when_quality_is_stable() {
        val trend =
            analyzer.analyze(
                listOf(
                    good(0.70, 1L),
                    good(0.90, 2L),
                    good(0.72, 3L)
                )
            )

        assertEquals(
            0,
            trend.qualityTransitionCount
        )

        assertEquals(
            1,
            trend.confidenceDirectionChanges
        )

        assertTrue(
            trend.oscillating
        )
    }

    @Test
    fun monotonic_confidence_improvement_is_not_oscillation() {
        val trend =
            analyzer.analyze(
                listOf(
                    good(0.60, 1L),
                    good(0.75, 2L),
                    good(0.90, 3L)
                )
            )

        assertEquals(
            RuntimeDecisionReflectionTrendState.IMPROVING,
            trend.state
        )

        assertEquals(
            0,
            trend.confidenceDirectionChanges
        )

        assertFalse(
            trend.oscillating
        )
    }

    @Test
    fun two_samples_are_never_labeled_oscillating() {
        val trend =
            analyzer.analyze(
                listOf(
                    good(0.90, 1L),
                    bad(0.60, 2L)
                )
            )

        assertEquals(
            1,
            trend.qualityTransitionCount
        )

        assertFalse(
            trend.oscillating
        )
    }

    private fun good(
        confidence: Double,
        recordedAt: Long
    ) =
        record(
            confidence = confidence,
            trustworthy = true,
            attention = false,
            recordedAt = recordedAt
        )

    private fun bad(
        confidence: Double,
        recordedAt: Long
    ) =
        record(
            confidence = confidence,
            trustworthy = false,
            attention = true,
            recordedAt = recordedAt
        )

    private fun record(
        confidence: Double,
        trustworthy: Boolean,
        attention: Boolean,
        recordedAt: Long
    ) =
        RuntimeDecisionReflectionRecord(
            insight =
                RuntimeDecisionReflectionInsight(
                    evidence =
                        RuntimeDecisionReflectionEvidence(
                            command = null,
                            decisionReason =
                                "temporal trend evidence",
                            confidence = confidence,
                            knowledgeUsed = true,
                            provenanceAvailable =
                                trustworthy,
                            provenanceValid =
                                if (trustworthy) {
                                    true
                                } else {
                                    false
                                },
                            provenanceDepth =
                                if (trustworthy) {
                                    1
                                } else {
                                    0
                                }
                        ),
                    trustworthyKnowledgeBasis =
                        trustworthy,
                    requiresAttention =
                        attention,
                    summary =
                        "temporal trend insight"
                ),
            recordedAt = recordedAt
        )
}
