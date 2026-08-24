package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleTransitionHistoryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "transition history knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun successful_transition_creates_history_entry() {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val historyStore =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(
                    stateStore
                ),
                stateStore,
                historyStore
            )

        val knowledge = knowledge()

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        val history =
            historyStore.history(knowledge)

        assertEquals(1, history.size)
        assertEquals(
            RuntimeKnowledgeLifecycleState.ACTIVE,
            history[0].to
        )
    }

    @Test
    fun rejected_transition_does_not_create_history_entry() {

        val stateStore =
            DefaultRuntimeKnowledgeLifecycleStateStore()

        val historyStore =
            DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(
                    stateStore
                ),
                stateStore,
                historyStore
            )

        val knowledge = knowledge()

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ARCHIVED
        )

        assertEquals(
            1,
            historyStore.history(knowledge).size
        )
    }
}
