package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class RuntimeCompositionKnowledgeRelevanceFeedbackContractTest {

    @Test
    fun real_experience_knowledge_must_beat_more_confident_irrelevant_knowledge_on_next_cycle() {
        val composition =
            DefaultRuntimeComposition()

        val first =
            composition
                .intelligenceOrchestrator()
                .process()

        val memory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()

        val generated =
            memory
                .availableKnowledge()
                .last()

        memory.remember(
            RuntimeKnowledge(
                statement = "completely unrelated external knowledge",
                confidence = 0.99,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = generated.createdAt + 1L
            )
        )

        val second =
            composition
                .intelligenceOrchestrator()
                .process()

        assertTrue(
            second.meaning.interpretation.contains(
                generated.statement
            ),
            "real experience-derived relevant knowledge must enrich the next meaning"
        )

        assertFalse(
            second.meaning.interpretation.contains(
                "completely unrelated external knowledge"
            ),
            "irrelevant higher-confidence knowledge must not override relevant experience knowledge"
        )

        assertTrue(
            first.meaning.interpretation.isNotBlank()
        )
    }
}
