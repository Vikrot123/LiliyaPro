package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionGovernanceExplainabilityStitchContractTest {

    @Test
    fun composition_explanation_snapshots_current_governance_state() {
        val composition =
            DefaultRuntimeComposition()

        val explanation =
            composition
                .decisionExplainer()
                .explain(
                    decision()
                )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            explanation
                .decisionQualityGovernanceAssessment
                ?.state
        )
    }

    @Test
    fun governance_change_is_visible_to_new_explanation() {
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

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            explanation
                .decisionQualityGovernanceAssessment
                ?.state
        )
    }

    @Test
    fun historical_explanation_keeps_original_governance_snapshot() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition
                .decisionExplainer()
                .explain(
                    decision()
                )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            before
                .decisionQualityGovernanceAssessment
                ?.state
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

        val after =
            composition
                .decisionExplainer()
                .explain(
                    decision()
                )

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            after
                .decisionQualityGovernanceAssessment
                ?.state
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            before
                .decisionQualityGovernanceAssessment
                ?.state,
            "historical explanation must preserve original governance snapshot"
        )
    }

    @Test
    fun explanation_recorder_preserves_governance_snapshot() {
        val composition =
            DefaultRuntimeComposition()

        val record =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(
                    decision()
                )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            record
                .explanation
                .decisionQualityGovernanceAssessment
                ?.state
        )
    }

    @Test
    fun prepare_runtime_preserves_explainer_owner_and_resets_governance_snapshot_source() {
        val composition =
            DefaultRuntimeComposition()

        val explainer =
            composition.decisionExplainer()

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
            explainer
                .explain(decision())
                .decisionQualityGovernanceAssessment
                ?.state
        )

        composition.prepareRuntime()

        assertSame(
            explainer,
            composition.decisionExplainer()
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            explainer
                .explain(decision())
                .decisionQualityGovernanceAssessment
                ?.state
        )
    }

    private fun decision() =
        RuntimeDecision(
            command = null,
            reason =
                "governance explainability stitch verification",
            confidence = 0.90,
            knowledgeSelection = null
        )

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "governance explainability attention fixture",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
