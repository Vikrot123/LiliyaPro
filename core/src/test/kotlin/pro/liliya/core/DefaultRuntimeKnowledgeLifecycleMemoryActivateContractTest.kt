package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory

class DefaultRuntimeKnowledgeLifecycleMemoryActivateContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "activate lifecycle knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun lifecycle_memory_activate_keeps_active_state() {

        val memory = DefaultRuntimeKnowledgeLifecycleMemory()
        val knowledge = knowledge()

        memory.create(knowledge)
        memory.activate(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.memory()
                .getLifecycleState(knowledge)
        )
    }
}
