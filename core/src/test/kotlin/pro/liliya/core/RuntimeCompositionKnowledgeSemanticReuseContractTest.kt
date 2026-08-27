package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionKnowledgeSemanticReuseContractTest {

    @Test
    fun experience_knowledge_can_be_semantically_retrieved_and_reused() {
        val composition =
            DefaultRuntimeComposition()

        composition
            .intelligenceOrchestrator()
            .process()

        val memory =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()
                .memory()

        val available =
            memory.availableKnowledge()

        assertTrue(
            available.isNotEmpty()
        )

        val generated =
            available.last()

        val query =
            generated.statement
                .replace(
                    "Consolidated",
                    "consolidated",
                    ignoreCase = true
                )

        val retrieved =
            memory.retrieveRelevant(query)

        assertTrue(
            retrieved.any {
                it.knowledge == generated
            }
        )

        val next =
            composition
                .intelligenceOrchestrator()
                .process()

        assertTrue(
            next.meaning.knowledgeSelection?.knowledge != null
        )
    }

    @Test
    fun semantic_retrieval_does_not_change_graph_query_contract() {
        val composition =
            DefaultRuntimeComposition()

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

        val graphResults =
            memory.query(
                generated.statement
            )

        val semanticResults =
            memory.retrieveRelevant(
                generated.statement
            )

        assertTrue(
            semanticResults.any {
                it.knowledge == generated
            }
        )

        assertEquals(
            graphResults,
            memory.query(
                generated.statement
            )
        )
    }
}
