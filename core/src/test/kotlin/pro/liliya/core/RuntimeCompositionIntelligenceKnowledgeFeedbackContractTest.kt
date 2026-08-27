package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionIntelligenceKnowledgeFeedbackContractTest {

    @Test
    fun knowledge_created_by_previous_intelligence_cycle_must_enrich_next_meaning() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .intelligenceOrchestrator()
                .process()

        assertFalse(
            first.meaning.interpretation.contains(
                "available knowledge",
                ignoreCase = true
            ),
            "first cycle must begin without previously available knowledge"
        )

        val second =
            composition
                .intelligenceOrchestrator()
                .process()

        assertTrue(
            second.meaning.interpretation.contains(
                "available knowledge",
                ignoreCase = true
            ),
            "knowledge created by the first cycle must enrich the next meaning interpretation"
        )

        assertTrue(
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()
                .availableKnowledge()
                .isNotEmpty(),
            "intelligence cycle must leave lifecycle-visible knowledge for the next cycle"
        )
    }
}
