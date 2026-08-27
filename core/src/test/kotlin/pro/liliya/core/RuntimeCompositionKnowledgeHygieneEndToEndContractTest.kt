package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionKnowledgeHygieneEndToEndContractTest {

    @Test
    fun repeated_learning_does_not_create_exact_statement_duplicates() {
        val composition =
            DefaultRuntimeComposition()

        repeat(3) {
            composition
                .intelligenceOrchestrator()
                .process()
        }

        val knowledge =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()
                .availableKnowledge()

        val normalized =
            knowledge.map {
                normalize(it.statement)
            }

        assertEquals(
            normalized.toSet().size,
            normalized.size
        )

        assertTrue(
            knowledge.isNotEmpty()
        )
    }

    private fun normalize(
        text: String
    ): String =
        text
            .lowercase()
            .replace(
                Regex("[^a-z0-9]+"),
                " "
            )
            .trim()
            .replace(
                Regex("\\s+"),
                " "
            )
}
