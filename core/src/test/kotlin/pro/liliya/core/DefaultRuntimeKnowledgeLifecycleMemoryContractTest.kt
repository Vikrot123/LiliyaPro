package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory

class DefaultRuntimeKnowledgeLifecycleMemoryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "runtime lifecycle knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun lifecycle_memory_creates_and_stores_knowledge() {

        val memory =
            DefaultRuntimeKnowledgeLifecycleMemory()

        memory.create(
            knowledge()
        )

        val result =
            memory.memory()
                .query(
                    "lifecycle"
                )

        assertEquals(
            1,
            result.size
        )
    }
}
