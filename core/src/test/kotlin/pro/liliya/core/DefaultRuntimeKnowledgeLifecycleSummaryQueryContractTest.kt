package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.DefaultRuntimeKnowledgeLifecycleHistoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.summary.DefaultRuntimeKnowledgeLifecycleSummaryQuery

class DefaultRuntimeKnowledgeLifecycleSummaryQueryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "summary knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun summary_contains_lifecycle_state_information() {

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
            DefaultRuntimeKnowledgeLifecycleSummaryQuery(
                DefaultRuntimeKnowledgeLifecycleHistoryQuery(store)
            )

        val summary =
            query.summary(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            summary.currentState
        )

        assertEquals(
            2,
            summary.transitionCount
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            summary.firstState
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            summary.lastState
        )
    }
}
