package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.DefaultRuntimeKnowledgeLifecycleHistoryQuery

class DefaultRuntimeKnowledgeLifecycleHistoryQueryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "history query knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun history_query_returns_last_transition() {

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

        val query =
            DefaultRuntimeKnowledgeLifecycleHistoryQuery(store)

        val last =
            query.lastTransition(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            last?.to
        )

        assertEquals(
            2,
            query.transitionCount(knowledge)
        )
    }

    @Test
    fun unknown_knowledge_has_no_history() {

        val store =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        assertEquals(
            emptyList(),
            store.history(knowledge())
        )
    }
}
