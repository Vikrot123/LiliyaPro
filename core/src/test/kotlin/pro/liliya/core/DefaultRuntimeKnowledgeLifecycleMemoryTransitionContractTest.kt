package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory

class DefaultRuntimeKnowledgeLifecycleMemoryTransitionContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "memory transition knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun lifecycle_memory_create_uses_transition_flow() {

        val memory =
            DefaultRuntimeKnowledgeLifecycleMemory()

        val knowledge = knowledge()

        memory.create(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.memory()
                .getLifecycleState(knowledge)
        )
    }
}
