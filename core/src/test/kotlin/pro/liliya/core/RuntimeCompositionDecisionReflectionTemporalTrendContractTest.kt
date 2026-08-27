package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionReflectionTemporalTrendContractTest {

    @Test
    fun composition_history_exposes_temporal_oscillation() {
        val composition =
            DefaultRuntimeComposition()

        record(
            composition,
            confidence = 0.60,
            knowledgeUsed = true
        )

        record(
            composition,
            confidence = 0.90,
            knowledgeUsed = false
        )

        record(
            composition,
            confidence = 0.60,
            knowledgeUsed = true
        )

        val trend =
            composition
                .decisionReflectionTrendQuery()
                .currentTrend()

        assertEquals(
            3,
            trend.sampleCount
        )

        assertTrue(
            trend.oscillating
        )

        assertTrue(
            trend.qualityTransitionCount >= 2 ||
                trend.confidenceDirectionChanges >= 1
        )
    }

    @Test
    fun prepare_runtime_resets_all_temporal_metrics_but_preserves_query_owner() {
        val composition =
            DefaultRuntimeComposition()

        val query =
            composition
                .decisionReflectionTrendQuery()

        record(
            composition,
            confidence = 0.60,
            knowledgeUsed = true
        )

        record(
            composition,
            confidence = 0.90,
            knowledgeUsed = false
        )

        record(
            composition,
            confidence = 0.60,
            knowledgeUsed = true
        )

        assertTrue(
            query.currentTrend().oscillating
        )

        composition.prepareRuntime()

        assertSame(
            query,
            composition
                .decisionReflectionTrendQuery()
        )

        val after =
            query.currentTrend()

        assertEquals(
            0,
            after.sampleCount
        )

        assertEquals(
            0,
            after.qualityTransitionCount
        )

        assertEquals(
            0,
            after.confidenceDirectionChanges
        )

        assertFalse(
            after.oscillating
        )
    }

    private fun record(
        composition: DefaultRuntimeComposition,
        confidence: Double,
        knowledgeUsed: Boolean
    ) {
        val explanation =
            RuntimeDecisionExplanation(
                command = null,
                decisionReason =
                    "composition temporal trend",
                confidence = confidence,
                knowledgeStatement =
                    if (knowledgeUsed) {
                        "knowledge without provenance"
                    } else {
                        null
                    },
                knowledgeSelectionReason =
                    if (knowledgeUsed) {
                        RuntimeKnowledgeSelectionReason
                            .RELEVANT_POOL
                    } else {
                        RuntimeKnowledgeSelectionReason.EMPTY
                    },
                knowledgeRelevanceScore =
                    if (knowledgeUsed) {
                        1.0
                    } else {
                        0.0
                    }
            )

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                explanation
            )
    }
}
