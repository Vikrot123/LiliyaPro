package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeDecisionGovernanceExplainabilitySnapshotIsolationContractTest {

    @Test
    fun separate_compositions_produce_independent_governance_snapshots() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        repeat(2) {
            first
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    attentionExplanation()
                )

            first
                .decisionQualityRecorder()
                .recordCurrent()
        }

        val firstExplanation =
            first
                .decisionExplainer()
                .explain(
                    decision()
                )

        val secondExplanation =
            second
                .decisionExplainer()
                .explain(
                    decision()
                )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            firstExplanation
                .decisionQualityGovernanceAssessment
                ?.state
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            secondExplanation
                .decisionQualityGovernanceAssessment
                ?.state
        )

        assertNotSame(
            firstExplanation
                .decisionQualityGovernanceAssessment,
            secondExplanation
                .decisionQualityGovernanceAssessment
        )
    }

    @Test
    fun recorded_explanation_keeps_governance_snapshot_after_later_change() {
        val composition =
            DefaultRuntimeComposition()

        val record =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(
                    decision()
                )

        val snapshot =
            record
                .explanation
                .decisionQualityGovernanceAssessment
                ?: error("governance snapshot expected")

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            snapshot.state
        )

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

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            composition
                .decisionExplainer()
                .explain(decision())
                .decisionQualityGovernanceAssessment
                ?.state
        )

        assertSame(
            snapshot,
            record
                .explanation
                .decisionQualityGovernanceAssessment
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            snapshot.state
        )
    }

    @Test
    fun recorded_snapshot_survives_prepare_as_historical_value() {
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

        val explanation =
            composition
                .decisionExplainer()
                .explain(
                    decision()
                )

        val snapshot =
            explanation
                .decisionQualityGovernanceAssessment
                ?: error("governance snapshot expected")

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            snapshot.state
        )

        composition.prepareRuntime()

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            snapshot.state,
            "historical governance snapshot must survive prepare"
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            composition
                .decisionExplainer()
                .explain(decision())
                .decisionQualityGovernanceAssessment
                ?.state
        )
    }

    @Test
    fun explanation_history_is_cleared_without_mutating_previously_returned_record() {
        val composition =
            DefaultRuntimeComposition()

        val record =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(
                    decision()
                )

        val snapshot =
            record
                .explanation
                .decisionQualityGovernanceAssessment
                ?: error("governance snapshot expected")

        assertEquals(
            1,
            composition
                .decisionExplanationHistory()
                .records()
                .size
        )

        composition.prepareRuntime()

        assertEquals(
            0,
            composition
                .decisionExplanationHistory()
                .records()
                .size
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            snapshot.state
        )
    }

    private fun decision() =
        RuntimeDecision(
            command = null,
            reason =
                "governance snapshot isolation verification",
            confidence = 0.90,
            knowledgeSelection = null
        )

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "governance snapshot attention fixture",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
