package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.RuntimeDecisionQualityAdvisoryLevel
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityAdvisoryHistoryContractTest {

    @Test
    fun composition_owns_stable_advisory_history_and_recorder() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityAdvisoryHistory(),
            composition.decisionQualityAdvisoryHistory()
        )

        assertSame(
            composition.decisionQualityAdvisoryRecorder(),
            composition.decisionQualityAdvisoryRecorder()
        )
    }

    @Test
    fun separate_compositions_own_independent_advisory_histories() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityAdvisoryHistory(),
            second.decisionQualityAdvisoryHistory()
        )
    }

    @Test
    fun reading_current_advisory_does_not_record_history() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .decisionQualityAdvisoryQuery()
            .currentAdvisory()

        composition
            .decisionQualityAdvisoryQuery()
            .currentAdvisory()

        assertTrue(
            composition
                .decisionQualityAdvisoryHistory()
                .records()
                .isEmpty()
        )
    }

    @Test
    fun explicit_recording_snapshots_current_advisory() {
        val composition =
            DefaultRuntimeComposition()

        repeat(2) {
            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    attentionExplanation()
                )

            composition
                .decisionQualityRecorder()
                .recordCurrent()
        }

        val record =
            composition
                .decisionQualityAdvisoryRecorder()
                .recordCurrent()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            record.advisory.level
        )

        assertEquals(
            listOf(record),
            composition
                .decisionQualityAdvisoryHistory()
                .records()
        )
    }

    @Test
    fun repeated_explicit_recording_preserves_historical_snapshots() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .decisionQualityAdvisoryRecorder()
                .recordCurrent()

        repeat(2) {
            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    attentionExplanation()
                )

            composition
                .decisionQualityRecorder()
                .recordCurrent()
        }

        val second =
            composition
                .decisionQualityAdvisoryRecorder()
                .recordCurrent()

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.OBSERVE,
            first.advisory.level
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryLevel.REVIEW,
            second.advisory.level
        )

        assertEquals(
            listOf(
                first,
                second
            ),
            composition
                .decisionQualityAdvisoryHistory()
                .records()
        )
    }

    @Test
    fun prepare_runtime_clears_advisory_history_but_preserves_owner_identity() {
        val composition =
            DefaultRuntimeComposition()

        val history =
            composition
                .decisionQualityAdvisoryHistory()

        val recorder =
            composition
                .decisionQualityAdvisoryRecorder()

        recorder.recordCurrent()

        assertEquals(
            1,
            history.records().size
        )

        composition.prepareRuntime()

        assertSame(
            history,
            composition
                .decisionQualityAdvisoryHistory()
        )

        assertSame(
            recorder,
            composition
                .decisionQualityAdvisoryRecorder()
        )

        assertTrue(
            history.records().isEmpty()
        )
    }

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "advisory history attention",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
