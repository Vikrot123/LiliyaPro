package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.advisory.trend.RuntimeDecisionQualityAdvisoryTrendState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityAdvisoryTrendContractTest {

    @Test
    fun composition_owns_stable_advisory_trend_analyzer_and_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityAdvisoryTrendAnalyzer(),
            composition.decisionQualityAdvisoryTrendAnalyzer()
        )

        assertSame(
            composition.decisionQualityAdvisoryTrendQuery(),
            composition.decisionQualityAdvisoryTrendQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_advisory_trend_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityAdvisoryTrendQuery(),
            second.decisionQualityAdvisoryTrendQuery()
        )
    }

    @Test
    fun explicit_advisory_records_drive_composition_trend() {
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

        val trend =
            composition
                .decisionQualityAdvisoryTrendQuery()
                .currentTrend()

        assertEquals(
            2,
            trend.sampleCount
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState.DEGRADING,
            trend.state
        )
    }

    @Test
    fun prepare_runtime_resets_advisory_trend_without_replacing_owner() {
        val composition =
            DefaultRuntimeComposition()

        val query =
            composition
                .decisionQualityAdvisoryTrendQuery()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState.STABLE,
            query.currentTrend().state
        )

        composition.prepareRuntime()

        assertSame(
            query,
            composition
                .decisionQualityAdvisoryTrendQuery()
        )

        assertEquals(
            RuntimeDecisionQualityAdvisoryTrendState
                .INSUFFICIENT_DATA,
            query.currentTrend().state
        )
    }

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "advisory temporal trend attention",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
