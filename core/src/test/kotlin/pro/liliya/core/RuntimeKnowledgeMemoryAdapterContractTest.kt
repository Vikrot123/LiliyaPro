package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.memory.adapter.RuntimeKnowledgeMemoryAdapter
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.RuntimeKnowledgeGraphRankingResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeKnowledgeMemoryAdapterContractTest {

    @Test
    fun store_should_delegate_to_knowledge_memory() {

        val memory =
            FakeKnowledgeMemory()

        val adapter =
            RuntimeKnowledgeMemoryAdapter(memory)

        adapter.store(
            RuntimeMemoryEntry(
                id = "1",
                content = "adapter test",
                type = RuntimeMemoryType.SEMANTIC,
                confidence = 0.9,
                createdAt = 1L
            )
        )

        assertEquals(
            1,
            memory.rememberCount
        )
    }

    @Test
    fun search_should_convert_knowledge_results_to_memory_entries() {

        val memory =
            FakeKnowledgeMemoryWithQuery()

        val adapter =
            RuntimeKnowledgeMemoryAdapter(memory)

        val result =
            adapter.search("adapter")

        assertEquals(
            1,
            result.size
        )

        assertEquals(
            "adapter knowledge",
            result[0].content
        )

        assertEquals(
            0.8,
            result[0].confidence
        )

        assertEquals(
            RuntimeMemoryType.SEMANTIC,
            result[0].type
        )
    }


    private class FakeKnowledgeMemory :
        RuntimeKnowledgeMemory {

        var rememberCount = 0

        override fun remember(
            knowledge: RuntimeKnowledge
        ) {
            rememberCount++
        }

        override fun forget(
            knowledge: RuntimeKnowledge
        ) {
        }

        override fun associate(
            source: RuntimeKnowledge,
            target: RuntimeKnowledge,
            type: RuntimeKnowledgeAssociationType
        ) {
        }

        override fun query(
            text: String
        ): List<RuntimeKnowledgeGraphRankingResult> {
            return emptyList()
        }

        override fun getLifecycleState(
            knowledge: RuntimeKnowledge
        ): RuntimeKnowledgeLifecycleState? {
            return null
        }
    }

    private class FakeKnowledgeMemoryWithQuery :
        RuntimeKnowledgeMemory {

        override fun remember(
            knowledge: RuntimeKnowledge
        ) {
        }

        override fun forget(
            knowledge: RuntimeKnowledge
        ) {
        }

        override fun associate(
            source: RuntimeKnowledge,
            target: RuntimeKnowledge,
            type: RuntimeKnowledgeAssociationType
        ) {
        }

        override fun query(
            text: String
        ): List<RuntimeKnowledgeGraphRankingResult> {

            val knowledge =
                RuntimeKnowledge(
                    statement = "adapter knowledge",
                    confidence = 0.8,
                    source = pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource.EXPERIENCE,
                    createdAt = 1L
                )

            val node =
                pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode(
                    knowledge = knowledge,
                    createdAt = 1L
                )

            val queryResult =
                pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQueryResult(
                    node = node,
                    relevance = 1.0,
                    reason = "test"
                )

            return listOf(
                RuntimeKnowledgeGraphRankingResult(
                    result = queryResult,
                    score = 0.8,
                    reason = "test"
                )
            )
        }

        override fun getLifecycleState(
            knowledge: RuntimeKnowledge
        ): RuntimeKnowledgeLifecycleState? {
            return null
        }
    }


}
