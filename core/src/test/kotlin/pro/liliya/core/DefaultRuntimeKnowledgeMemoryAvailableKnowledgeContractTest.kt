package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeMemoryAvailableKnowledgeContractTest {

    @Test
    fun available_knowledge_must_hide_archived_entries() {
        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val memory =
            DefaultRuntimeKnowledgeMemory(
                lifecycleStateStore = stateStore
            )

        val active =
            knowledge("active knowledge")

        val review =
            knowledge("review knowledge")

        val archived =
            knowledge("archived knowledge")

        memory.remember(active)
        memory.remember(review)
        memory.remember(archived)

        stateStore.setState(
            active,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        stateStore.setState(
            review,
            RuntimeKnowledgeLifecycleState.REVIEW
        )

        stateStore.setState(
            archived,
            RuntimeKnowledgeLifecycleState.ARCHIVED
        )

        assertEquals(
            listOf(
                active,
                review
            ),
            memory.availableKnowledge(),
            "available knowledge must preserve visible knowledge order and exclude archived entries"
        )
    }

    private fun knowledge(
        statement: String
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )
    }
}
