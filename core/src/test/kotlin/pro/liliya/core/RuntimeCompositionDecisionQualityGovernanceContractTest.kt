package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.quality.governance.RuntimeDecisionQualityGovernanceState
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionQualityGovernanceContractTest {

    @Test
    fun composition_owns_stable_governance_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionQualityGovernanceQuery(),
            composition.decisionQualityGovernanceQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_governance_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionQualityGovernanceQuery(),
            second.decisionQualityGovernanceQuery()
        )
    }

    @Test
    fun empty_observability_state_is_insufficient_governance() {
        val composition =
            DefaultRuntimeComposition()

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            composition
                .decisionQualityGovernanceQuery()
                .currentAssessment()
                .state
        )
    }

    @Test
    fun worsening_observability_produces_caution_without_policy_mutation() {
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

        val assessment =
            composition
                .decisionQualityGovernanceQuery()
                .currentAssessment()

        assertEquals(
            RuntimeDecisionQualityGovernanceState.REVIEW,
            assessment.state
        )
    }

    @Test
    fun prepare_runtime_resets_governance_read_model_without_replacing_owner() {
        val composition =
            DefaultRuntimeComposition()

        val query =
            composition
                .decisionQualityGovernanceQuery()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        composition
            .decisionQualityAdvisoryRecorder()
            .recordCurrent()

        composition.prepareRuntime()

        assertSame(
            query,
            composition.decisionQualityGovernanceQuery()
        )

        assertEquals(
            RuntimeDecisionQualityGovernanceState
                .INSUFFICIENT_DATA,
            query.currentAssessment().state
        )
    }

    private fun attentionExplanation() =
        RuntimeDecisionExplanation(
            command = null,
            decisionReason =
                "governance assessment attention",
            confidence = 0.60,
            knowledgeStatement =
                "knowledge without provenance",
            knowledgeSelectionReason =
                RuntimeKnowledgeSelectionReason
                    .RELEVANT_POOL,
            knowledgeRelevanceScore = 1.0
        )
}
