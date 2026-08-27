package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.decision.quality.governance.trend.RuntimeDecisionQualityGovernanceTrendState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityGovernanceTrendContractTest {

    @Test
    fun composition_owns_stable_governance_trend_analyzer_and_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityGovernanceTrendAnalyzer(),
            composition.decisionQualityGovernanceTrendAnalyzer()
        )

        assertSame(
            composition.decisionQualityGovernanceTrendQuery(),
            composition.decisionQualityGovernanceTrendQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_governance_trend_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityGovernanceTrendQuery(),
            second.decisionQualityGovernanceTrendQuery()
        )
    }

    @Test
    fun explicit_governance_records_drive_composition_trend() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .decisionQualityGovernanceRecorder()
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

        composition
            .decisionQualityGovernanceRecorder()
            .recordCurrent()

        val trend =
            composition
                .decisionQualityGovernanceTrendQuery()
                .currentTrend()

        assertEquals(
            2,
            trend.sampleCount
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            trend.firstGovernanceState
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            trend.latestGovernanceState
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.DEGRADING,
            trend.state
        )
    }

    @Test
    fun prepare_runtime_resets_governance_trend_without_replacing_owner() {
        val composition =
            DefaultRuntimeComposition()

        val query =
            composition
                .decisionQualityGovernanceTrendQuery()

        composition
            .decisionQualityGovernanceRecorder()
            .recordCurrent()

        composition
            .decisionQualityGovernanceRecorder()
            .recordCurrent()

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState.STABLE,
            query.currentTrend().state
        )

        composition.prepareRuntime()

        assertSame(
            query,
            composition
                .decisionQualityGovernanceTrendQuery()
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceTrendState
                .INSUFFICIENT_DATA,
            query.currentTrend().state
        )
    }

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "governance temporal trend attention",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
