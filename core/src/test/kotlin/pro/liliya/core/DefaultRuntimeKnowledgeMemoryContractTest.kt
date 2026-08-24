package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory

class DefaultRuntimeKnowledgeMemoryContractTest {

    private fun knowledge(
        statement: String,
        confidence: Double
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun memory_remember_and_query_returns_ranked_knowledge() {

        val memory =
            DefaultRuntimeKnowledgeMemory()

        memory.remember(
            knowledge(
                "runtime health",
                0.9
            )
        )

        memory.remember(
            knowledge(
                "memory state",
                0.5
            )
        )

        val result =
            memory.query(
                "runtime"
            )

        assertEquals(
            1,
            result.size
        )

        assertEquals(
            "runtime health",
            result.first()
                .result
                .node
                .knowledge
                .statement
        )
    }
}
