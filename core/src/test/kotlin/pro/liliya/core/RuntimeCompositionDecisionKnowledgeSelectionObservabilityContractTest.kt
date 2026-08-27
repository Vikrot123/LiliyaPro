package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionDecisionKnowledgeSelectionObservabilityContractTest {

    @Test
    fun decision_must_preserve_intelligence_knowledge_selection_across_cycles() {
        val composition =
            DefaultRuntimeComposition()

        val firstIntelligence =
            composition
                .intelligenceOrchestrator()
                .process()

        val firstDecision =
            composition
                .decisionEngine()
                .decide(firstIntelligence)

        val firstSelection =
            assertNotNull(
                firstDecision.knowledgeSelection
            )

        assertNull(
            firstSelection.knowledge
        )

        assertEquals(
            RuntimeKnowledgeSelectionReason.EMPTY,
            firstSelection.selectionReason
        )

        assertEquals(
            0.0,
            firstSelection.relevanceScore
        )

        val secondIntelligence =
            composition
                .intelligenceOrchestrator()
                .process()

        val secondDecision =
            composition
                .decisionEngine()
                .decide(secondIntelligence)

        val secondSelection =
            assertNotNull(
                secondDecision.knowledgeSelection
            )

        assertNotNull(
            secondSelection.knowledge
        )

        assertEquals(
            secondIntelligence.meaning.knowledgeSelection,
            secondDecision.knowledgeSelection
        )

        assertEquals(
            secondIntelligence.meaning.confidence,
            secondDecision.confidence
        )
    }
}
