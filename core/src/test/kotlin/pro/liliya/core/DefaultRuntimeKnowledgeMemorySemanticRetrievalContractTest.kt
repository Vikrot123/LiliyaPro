package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory

class DefaultRuntimeKnowledgeMemorySemanticRetrievalContractTest {

    @Test
    fun available_knowledge_can_be_retrieved_semantically() {
        val memory =
            DefaultRuntimeKnowledgeMemory()

        val related =
            knowledge(
                "runtime operational state remained stable",
                0.80,
                1L
            )

        val unrelated =
            knowledge(
                "network recovery completed",
                0.99,
                2L
            )

        memory.remember(related)
        memory.remember(unrelated)

        val results =
            memory.retrieveRelevant(
                "Runtime maintains stable operational state"
            )

        assertEquals(
            listOf(related),
            results.map {
                it.knowledge
            }
        )

        assertEquals(
            0.8,
            results.single().relevance
        )
    }

    @Test
    fun semantic_memory_retrieval_preserves_relevance_ranking() {
        val memory =
            DefaultRuntimeKnowledgeMemory()

        val strongerRelevance =
            knowledge(
                "runtime maintains stable operational state",
                0.70,
                1L
            )

        val weakerRelevance =
            knowledge(
                "runtime operational state unrelated",
                0.99,
                2L
            )

        memory.remember(weakerRelevance)
        memory.remember(strongerRelevance)

        val results =
            memory.retrieveRelevant(
                "runtime maintains stable operational state"
            )

        assertEquals(
            strongerRelevance,
            results.first().knowledge
        )

        assertTrue(
            results.first().relevance >
                results.last().relevance
        )
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = createdAt
        )
}
