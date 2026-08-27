package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionDecisionExplainerOwnershipContractTest {

    @Test
    fun composition_owns_stable_decision_explainer() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.decisionExplainer(),
            composition.decisionExplainer()
        )
    }

    @Test
    fun composition_explainer_preserves_real_decision_diagnostics() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .intelligenceOrchestrator()
            .process()

        val intelligence =
            composition
                .intelligenceOrchestrator()
                .process()

        val decision =
            composition
                .decisionEngine()
                .decide(intelligence)

        val explanation =
            composition
                .decisionExplainer()
                .explain(decision)

        val selection =
            assertNotNull(
                decision.knowledgeSelection
            )

        assertEquals(
            decision.command,
            explanation.command
        )

        assertEquals(
            decision.reason,
            explanation.decisionReason
        )

        assertEquals(
            decision.confidence,
            explanation.confidence
        )

        assertEquals(
            selection.knowledge?.statement,
            explanation.knowledgeStatement
        )

        assertEquals(
            selection.selectionReason,
            explanation.knowledgeSelectionReason
        )

        assertEquals(
            selection.relevanceScore,
            explanation.knowledgeRelevanceScore
        )
    }
}
