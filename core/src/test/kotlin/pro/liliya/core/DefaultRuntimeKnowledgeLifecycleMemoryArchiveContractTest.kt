package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.DefaultRuntimeKnowledgeLifecycleMemory

class DefaultRuntimeKnowledgeLifecycleMemoryArchiveContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "archive lifecycle knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun lifecycle_memory_archive_requires_review_transition() {

        val memory = DefaultRuntimeKnowledgeLifecycleMemory()
        val knowledge = knowledge()

        memory.create(knowledge)
        memory.archive(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            memory.memory()
                .getLifecycleState(knowledge)
        )

        memory.revise(knowledge)
        memory.archive(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.ARCHIVED,
            memory.memory()
                .getLifecycleState(knowledge)
        )
    }
}
