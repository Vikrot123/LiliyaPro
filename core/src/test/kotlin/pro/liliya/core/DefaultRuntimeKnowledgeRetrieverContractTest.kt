package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.DefaultRuntimeKnowledgeRetriever
import pro.liliya.core.runtime.intelligence.knowledge.retrieval.RuntimeKnowledgeQuery

class DefaultRuntimeKnowledgeRetrieverContractTest {

    private fun knowledge(
        statement: String
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun matching_statement_is_retrieved() {

        val result =
            DefaultRuntimeKnowledgeRetriever()
                .retrieve(
                    query = RuntimeKnowledgeQuery("runtime"),
                    knowledge = listOf(
                        knowledge("runtime health status")
                    )
                )

        assertEquals(
            1,
            result.size
        )
    }

    @Test
    fun unknown_query_returns_empty_result() {

        val result =
            DefaultRuntimeKnowledgeRetriever()
                .retrieve(
                    query = RuntimeKnowledgeQuery("unknown"),
                    knowledge = listOf(
                        knowledge("runtime health status")
                    )
                )

        assertEquals(
            0,
            result.size
        )
    }
}
