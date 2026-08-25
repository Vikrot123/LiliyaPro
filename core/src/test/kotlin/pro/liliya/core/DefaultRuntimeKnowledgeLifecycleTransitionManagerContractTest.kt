package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleTransitionManagerContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "transition knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun allows_valid_lifecycle_transition() {

        val store = DefaultRuntimeKnowledgeLifecycleStateStore()
        val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(store),
                store,
                historyStore,
            )

        assertTrue(
            manager.transition(
                knowledge(),
                RuntimeKnowledgeLifecycleState.ACTIVE
            )
        )
    }

    @Test
    fun blocks_invalid_active_to_archived_transition() {

        val store = DefaultRuntimeKnowledgeLifecycleStateStore()
        val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()
        val knowledge = knowledge()

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(store),
                store,
                historyStore,
            )

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        assertFalse(
            manager.transition(
                knowledge,
                RuntimeKnowledgeLifecycleState.ARCHIVED
            )
        )
    }

    @Test
    fun allows_review_to_archived_transition() {

        val store = DefaultRuntimeKnowledgeLifecycleStateStore()
        val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()
        val knowledge = knowledge()

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(store),
                store,
                historyStore,
            )

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.REVIEW
        )

        assertTrue(
            manager.transition(
                knowledge,
                RuntimeKnowledgeLifecycleState.ARCHIVED
            )
        )
    }

    @Test
    fun blocks_archived_to_active_transition() {

        val store = DefaultRuntimeKnowledgeLifecycleStateStore()
        val historyStore = DefaultRuntimeKnowledgeLifecycleHistoryStore()
        val knowledge = knowledge()

        val manager =
            DefaultRuntimeKnowledgeLifecycleTransitionManager(
                DefaultRuntimeKnowledgeLifecycleStateQuery(store),
                store,
                historyStore,
            )

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.REVIEW
        )

        manager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ARCHIVED
        )

        assertFalse(
            manager.transition(
                knowledge,
                RuntimeKnowledgeLifecycleState.ACTIVE
            )
        )
    }

}
