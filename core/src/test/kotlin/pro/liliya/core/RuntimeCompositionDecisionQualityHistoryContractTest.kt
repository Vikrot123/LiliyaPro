package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityHistoryContractTest {

    @Test
    fun composition_owns_stable_quality_history_and_recorder() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityHistory(),
            composition.decisionQualityHistory()
        )

        assertSame(
            composition.decisionQualityRecorder(),
            composition.decisionQualityRecorder()
        )
    }

    @Test
    fun separate_compositions_own_independent_quality_histories() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityHistory(),
            second.decisionQualityHistory()
        )
    }

    @Test
    fun recorder_snapshots_current_quality_without_mutating_query_behavior() {
        val composition =
            DefaultRuntimeComposition()

        assertNull(
            composition
                .decisionQualityRecorder()
                .recordCurrent()
        )

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                attentionExplanation(
                    0.60
                )
            )

        val before =
            composition
                .decisionQualityQuery()
                .currentAssessment()
                ?: error(
                    "quality assessment expected"
                )

        val record =
            composition
                .decisionQualityRecorder()
                .recordCurrent()
                ?: error(
                    "quality record expected"
                )

        assertEquals(
            before,
            record.assessment
        )

        assertEquals(
            1,
            composition
                .decisionQualityHistory()
                .records()
                .size
        )

        assertEquals(
            before,
            composition
                .decisionQualityQuery()
                .currentAssessment()
        )
    }

    @Test
    fun repeated_explicit_recording_preserves_historical_snapshots() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                neutralExplanation(
                    0.70
                )
            )

        composition
            .decisionQualityRecorder()
            .recordCurrent()

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                attentionExplanation(
                    0.50
                )
            )

        composition
            .decisionQualityRecorder()
            .recordCurrent()

        val records =
            composition
                .decisionQualityHistory()
                .records()

        assertEquals(
            2,
            records.size
        )

        assertTrue(
            records[0].assessment !=
                records[1].assessment
        )
    }

    @Test
    fun prepare_runtime_clears_quality_history_but_preserves_owner_identity() {
        val composition =
            DefaultRuntimeComposition()

        val history =
            composition
                .decisionQualityHistory()

        val recorder =
            composition
                .decisionQualityRecorder()

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                attentionExplanation(
                    0.60
                )
            )

        composition
            .decisionQualityRecorder()
            .recordCurrent()

        assertEquals(
            1,
            history.records().size
        )

        composition.prepareRuntime()

        assertSame(
            history,
            composition
                .decisionQualityHistory()
        )

        assertSame(
            recorder,
            composition
                .decisionQualityRecorder()
        )

        assertTrue(
            history.records().isEmpty()
        )

        assertNull(
            composition
                .decisionQualityQuery()
                .currentAssessment()
        )
    }

    private fun attentionExplanation(
        confidence: Double
    ) =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "quality history attention",
            confidence = confidence,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )

    private fun neutralExplanation(
        confidence: Double
    ) =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "quality history neutral",
            confidence = confidence,
            knowledgeStatement = null,
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason.EMPTY,
            knowledgeRelevanceScore = 0.0
        )
}
