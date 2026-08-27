package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason

class RuntimeCompositionIntelligenceKnowledgeSelectionObservabilityContractTest {

    @Test
    fun intelligence_result_must_preserve_meaning_selection_diagnostics_across_cycles() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .intelligenceOrchestrator()
                .process()

        val firstSelection =
            assertNotNull(
                first.meaning.knowledgeSelection
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

        val second =
            composition
                .intelligenceOrchestrator()
                .process()

        val secondSelection =
            assertNotNull(
                second.meaning.knowledgeSelection
            )

        assertNotNull(
            secondSelection.knowledge
        )

        assertTrue(
            secondSelection.selectionReason !=
                RuntimeKnowledgeSelectionReason.EMPTY
        )

        assertTrue(
            secondSelection.relevanceScore >= 0.0
        )
    }
}
