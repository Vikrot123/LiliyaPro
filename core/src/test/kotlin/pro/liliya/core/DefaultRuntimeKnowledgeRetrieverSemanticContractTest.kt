package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.DefaultRuntimeKnowledgeRetriever
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.RuntimeKnowledgeQuery

class DefaultRuntimeKnowledgeRetrieverSemanticContractTest {

    private val retriever =
        DefaultRuntimeKnowledgeRetriever()

    @Test
    fun related_knowledge_is_retrieved_without_exact_substring() {
        val related =
            knowledge(
                statement =
                    "runtime operational state remained stable",
                confidence = 0.8,
                createdAt = 1L
            )

        val unrelated =
            knowledge(
                statement = "network recovery completed",
                confidence = 0.99,
                createdAt = 2L
            )

        val results =
            retriever.retrieve(
                query =
                    RuntimeKnowledgeQuery(
                        "Runtime maintains stable operational state"
                    ),
                knowledge =
                    listOf(
                        unrelated,
                        related
                    )
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
    fun retrieval_is_ranked_by_relevance_before_confidence() {
        val strongerRelevance =
            knowledge(
                statement =
                    "runtime maintains stable operational state",
                confidence = 0.7,
                createdAt = 1L
            )

        val weakerRelevance =
            knowledge(
                statement =
                    "runtime operational state unrelated",
                confidence = 0.99,
                createdAt = 2L
            )

        val results =
            retriever.retrieve(
                query =
                    RuntimeKnowledgeQuery(
                        "runtime maintains stable operational state"
                    ),
                knowledge =
                    listOf(
                        weakerRelevance,
                        strongerRelevance
                    )
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

    @Test
    fun equal_relevance_uses_confidence_then_recency() {
        val older =
            knowledge(
                statement = "runtime stable operational state",
                confidence = 0.9,
                createdAt = 1L
            )

        val newer =
            knowledge(
                statement = "runtime stable operational state",
                confidence = 0.9,
                createdAt = 2L
            )

        val results =
            retriever.retrieve(
                query =
                    RuntimeKnowledgeQuery(
                        "runtime stable operational state"
                    ),
                knowledge =
                    listOf(
                        older,
                        newer
                    )
            )

        assertEquals(
            newer,
            results.first().knowledge
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
