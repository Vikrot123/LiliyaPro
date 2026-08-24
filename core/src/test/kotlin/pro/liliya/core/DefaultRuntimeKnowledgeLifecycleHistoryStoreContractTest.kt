package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry

class DefaultRuntimeKnowledgeLifecycleHistoryStoreContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "history knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun stores_lifecycle_history_entries_in_order() {

        val store =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val knowledge = knowledge()

        store.append(
            knowledge,
            RuntimeKnowledgeLifecycleHistoryEntry(
                from = null,
                to = RuntimeKnowledgeLifecycleState.ACTIVE,
                timestamp = 1L
            )
        )

        store.append(
            knowledge,
            RuntimeKnowledgeLifecycleHistoryEntry(
                from = RuntimeKnowledgeLifecycleState.ACTIVE,
                to = RuntimeKnowledgeLifecycleState.REVIEW,
                timestamp = 2L
            )
        )

        val history =
            store.history(knowledge)

        assertEquals(2, history.size)
        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            history[0].to
        )
        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            history[1].to
        )
    }

    @Test
    fun unknown_knowledge_has_empty_history() {

        val store =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        assertEquals(
            emptyList(),
            store.history(knowledge())
        )
    }
}
