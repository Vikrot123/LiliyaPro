package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.history.DefaultRuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.DefaultRuntimeDecisionReflectionTrendQuery
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class RuntimeDecisionReflectionTrendBoundedWindowContractTest {

    @Test
    fun trend_is_derived_only_from_records_remaining_in_bounded_history() {
        var now = 0L

        val history =
            DefaultRuntimeDecisionReflectionHistory(
                capacity = 3,
                clock = {
                    now += 1L
                    now
                }
            )

        val query =
            DefaultRuntimeDecisionReflectionTrendQuery(
                history = history
            )

        history.record(
            bad(0.60)
        )

        history.record(
            good(0.90)
        )

        history.record(
            bad(0.60)
        )

        val oscillating =
            query.currentTrend()

        assertEquals(
            3,
            oscillating.sampleCount
        )

        assertTrue(
            oscillating.oscillating
        )

        assertEquals(
            2,
            oscillating.qualityTransitionCount
        )

        history.record(
            bad(0.59)
        )

        val afterEviction =
            query.currentTrend()

        assertEquals(
            3,
            afterEviction.sampleCount
        )

        assertEquals(
            RuntimeDecisionReflectionTrendState.DEGRADING,
            afterEviction.state
        )

        assertEquals(
            1,
            afterEviction.qualityTransitionCount
        )

        assertFalse(
            afterEviction.oscillating
        )
    }

    @Test
    fun evicted_confidence_reversal_no_longer_affects_current_window() {
        var now = 0L

        val history =
            DefaultRuntimeDecisionReflectionHistory(
                capacity = 3,
                clock = {
                    now += 1L
                    now
                }
            )

        val query =
            DefaultRuntimeDecisionReflectionTrendQuery(
                history = history
            )

        history.record(
            good(0.70)
        )

        history.record(
            good(0.90)
        )

        history.record(
            good(0.72)
        )

        assertTrue(
            query.currentTrend().oscillating
        )

        history.record(
            good(0.71)
        )

        val current =
            query.currentTrend()

        assertEquals(
            3,
            current.sampleCount
        )

        assertEquals(
            0,
            current.confidenceDirectionChanges
        )

        assertFalse(
            current.oscillating
        )
    }

    private fun good(
        confidence: Double
    ) =
        insight(
            confidence = confidence,
            trustworthy = true,
            attention = false
        )

    private fun bad(
        confidence: Double
    ) =
        insight(
            confidence = confidence,
            trustworthy = false,
            attention = true
        )

    private fun insight(
        confidence: Double,
        trustworthy: Boolean,
        attention: Boolean
    ) =
        RuntimeDecisionReflectionInsight(
            evidence =
                RuntimeDecisionReflectionEvidence(
                    command = null,
                    decisionReason =
                        "bounded trend evidence",
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
                "bounded trend insight"
        )
}
