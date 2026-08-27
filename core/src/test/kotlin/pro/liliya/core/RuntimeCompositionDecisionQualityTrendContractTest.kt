package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.trend.RuntimeDecisionQualityTrendState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityTrendContractTest {

    @Test
    fun composition_owns_stable_quality_trend_analyzer_and_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityTrendAnalyzer(),
            composition.decisionQualityTrendAnalyzer()
        )

        assertSame(
            composition.decisionQualityTrendQuery(),
            composition.decisionQualityTrendQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_quality_trend_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityTrendQuery(),
            second.decisionQualityTrendQuery()
        )
    }

    @Test
    fun explicit_quality_records_drive_composition_quality_trend() {
        val composition =
            DefaultRuntimeComposition()

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

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                neutralExplanation(
                    0.90
                )
            )

        composition
            .decisionQualityRecorder()
            .recordCurrent()

        val trend =
            composition
                .decisionQualityTrendQuery()
                .currentTrend()

        assertEquals(
            2,
            trend.sampleCount
        )
    }

    @Test
    fun prepare_runtime_resets_quality_trend_through_quality_history_clear() {
        val composition =
            DefaultRuntimeComposition()

        val trendQuery =
            composition
                .decisionQualityTrendQuery()

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

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                neutralExplanation(
                    0.90
                )
            )

        composition
            .decisionQualityRecorder()
            .recordCurrent()

        assertEquals(
            2,
            trendQuery
                .currentTrend()
                .sampleCount
        )

        composition.prepareRuntime()

        assertSame(
            trendQuery,
            composition
                .decisionQualityTrendQuery()
        )

        assertEquals(
            RuntimeDecisionQualityTrendState
                .INSUFFICIENT_DATA,
            trendQuery
                .currentTrend()
                .state
        )

        assertEquals(
            0,
            trendQuery
                .currentTrend()
                .sampleCount
        )
    }

    private fun attentionExplanation(
        confidence: Double
    ) =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "quality trend attention",
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
                "quality trend neutral",
            confidence = confidence,
            knowledgeStatement = null,
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason.EMPTY,
            knowledgeRelevanceScore = 0.0
        )
}
