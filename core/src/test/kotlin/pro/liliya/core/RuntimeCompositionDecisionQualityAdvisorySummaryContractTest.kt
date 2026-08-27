package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.summary.RuntimeDecisionQualityAdvisorySummaryState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityAdvisorySummaryContractTest {

    @Test
    fun composition_owns_stable_advisory_summary_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityAdvisorySummaryQuery(),
            composition.decisionQualityAdvisorySummaryQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_advisory_summary_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityAdvisorySummaryQuery(),
            second.decisionQualityAdvisorySummaryQuery()
        )
    }

    @Test
    fun empty_advisory_history_is_insufficient_summary() {
        val composition =
            DefaultRuntimeComposition()

        val summary =
            composition
                .decisionQualityAdvisorySummaryQuery()
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState
                .INSUFFICIENT_DATA,
            summary.state
        )
    }

    @Test
    fun repeated_equal_advisories_produce_consistent_summary() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        val summary =
            composition
                .decisionQualityAdvisorySummaryQuery()
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState.CONSISTENT,
            summary.state
        )
    }

    @Test
    fun worsening_advisory_history_produces_degrading_summary() {
        val composition =
            DefaultRuntimeComposition()

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

        val summary =
            composition
                .decisionQualityAdvisorySummaryQuery()
                .currentSummary()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState.DEGRADING,
            summary.state
        )
    }

    @Test
    fun prepare_runtime_resets_summary_through_advisory_history_without_replacing_owner() {
        val composition =
            DefaultRuntimeComposition()

        val query =
            composition
                .decisionQualityAdvisorySummaryQuery()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState.CONSISTENT,
            query.currentSummary().state
        )

        composition.prepareRuntime()

        assertSame(
            query,
            composition
                .decisionQualityAdvisorySummaryQuery()
        )

        assertEquals(
            RuntimeDecisionQualityAdvisorySummaryState
                .INSUFFICIENT_DATA,
            query.currentSummary().state
        )
    }

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "advisory summary temporal degradation",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
