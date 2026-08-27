package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityGovernanceHistoryContractTest {

    @Test
    fun composition_owns_stable_governance_history_and_recorder() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityGovernanceHistory(),
            composition.decisionQualityGovernanceHistory()
        )

        assertSame(
            composition.decisionQualityGovernanceRecorder(),
            composition.decisionQualityGovernanceRecorder()
        )
    }

    @Test
    fun separate_compositions_own_independent_governance_histories() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityGovernanceHistory(),
            second.decisionQualityGovernanceHistory()
        )
    }

    @Test
    fun reading_governance_query_does_not_record_history() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .decisionQualityGovernanceQuery()
            .currentAssessment()

        composition
            .decisionQualityGovernanceQuery()
            .currentAssessment()

        assertTrue(
            composition
                .decisionQualityGovernanceHistory()
                .records()
                .isEmpty()
        )
    }

    @Test
    fun explicit_recorder_snapshots_current_governance() {
        val composition =
            DefaultRuntimeComposition()

        val record =
            composition
                .decisionQualityGovernanceRecorder()
                .recordCurrent()

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            record.assessment.state
        )

        assertEquals(
            listOf(record),
            composition
                .decisionQualityGovernanceHistory()
                .records()
        )
    }

    @Test
    fun historical_governance_records_preserve_distinct_snapshots() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .decisionQualityGovernanceRecorder()
                .recordCurrent()

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

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        val second =
            composition
                .decisionQualityGovernanceRecorder()
                .recordCurrent()

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            first.assessment.state
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            second.assessment.state
        )

        assertEquals(
            listOf(
                first,
                second
            ),
            composition
                .decisionQualityGovernanceHistory()
                .records()
        )
    }

    @Test
    fun prepare_runtime_clears_governance_history_but_preserves_owner_identity() {
        val composition =
            DefaultRuntimeComposition()

        val history =
            composition
                .decisionQualityGovernanceHistory()

        val recorder =
            composition
                .decisionQualityGovernanceRecorder()

        recorder.recordCurrent()

        assertEquals(
            1,
            history.records().size
        )

        composition.prepareRuntime()

        assertSame(
            history,
            composition
                .decisionQualityGovernanceHistory()
        )

        assertSame(
            recorder,
            composition
                .decisionQualityGovernanceRecorder()
        )

        assertTrue(
            history.records().isEmpty()
        )
    }

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "governance history attention",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
