package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.summary.RuntimeDecisionQualitySummaryState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualitySummaryContractTest {

    @Test
    fun composition_owns_stable_quality_summary_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualitySummaryQuery(),
            composition.decisionQualitySummaryQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_summary_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualitySummaryQuery(),
            second.decisionQualitySummaryQuery()
        )
    }

    @Test
    fun empty_quality_state_is_insufficient_summary() {
        val composition =
            DefaultRuntimeComposition()

        val summary =
            composition
                .decisionQualitySummaryQuery()
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualitySummaryState
                .INSUFFICIENT_DATA,
            summary.state
        )
    }

    @Test
    fun composition_summary_tracks_current_quality_and_quality_trend() {
        val composition =
            DefaultRuntimeComposition()

        repeat(2) {
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
        }

        val summary =
            composition
                .decisionQualitySummaryQuery()
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualitySummaryState.ATTENTION,
            summary.state
        )
    }

    @Test
    fun prepare_runtime_resets_summary_through_owned_read_models() {
        val composition =
            DefaultRuntimeComposition()

        val query =
            composition
                .decisionQualitySummaryQuery()

        repeat(2) {
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
        }

        assertEquals(
            RuntimeDecisionQualitySummaryState.ATTENTION,
            query.currentSummary().state
        )

        composition.prepareRuntime()

        assertSame(
            query,
            composition
                .decisionQualitySummaryQuery()
        )

        assertEquals(
            RuntimeDecisionQualitySummaryState
                .INSUFFICIENT_DATA,
            query.currentSummary().state
        )
    }

    private fun attentionExplanation(
        confidence: Double
    ) =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "quality summary attention",
            confidence = confidence,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
