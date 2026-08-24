package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeMemoryLifecycleRetrievalContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "archived runtime knowledge",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun archived_knowledge_is_hidden_from_memory_query() {

        val store = DefaultRuntimeKnowledgeLifecycleStateStore()

        val memory = DefaultRuntimeKnowledgeMemory(
            lifecycleStateStore = store
        )

        val knowledge = knowledge()

        memory.remember(knowledge)

        store.setState(
            knowledge,
            RuntimeKnowledgeLifecycleState.ARCHIVED
        )

        val result = memory.query("archived")

        assertEquals(
            0,
            result.size
        )
    }
}
