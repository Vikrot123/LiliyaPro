package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryEntry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.RuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleTransitionManager(
    private val stateQuery: RuntimeKnowledgeLifecycleStateQuery,
    private val stateStore: RuntimeKnowledgeLifecycleStateStore,
    private val historyStore: RuntimeKnowledgeLifecycleHistoryStore
) : RuntimeKnowledgeLifecycleTransitionManager {

    override fun transition(
        knowledge: RuntimeKnowledge,
        target: RuntimeKnowledgeLifecycleState
    ): Boolean {
        return synchronized(stateStore) {

            val current = stateQuery.getState(knowledge)

            val allowed = when {
            current == null &&
                target == RuntimeKnowledgeLifecycleState.ACTIVE ->
                true

            current == RuntimeKnowledgeLifecycleState.ACTIVE &&
                target == RuntimeKnowledgeLifecycleState.REVIEW ->
                true

            current == RuntimeKnowledgeLifecycleState.REVIEW &&
                target == RuntimeKnowledgeLifecycleState.ACTIVE ->
                true

            current == RuntimeKnowledgeLifecycleState.REVIEW &&
                target == RuntimeKnowledgeLifecycleState.ARCHIVED ->
                true

            else ->
                false
        }

            if (!allowed) {
                return@synchronized false
            }

        val historyEntry =
            RuntimeKnowledgeLifecycleHistoryEntry(
                from = current,
                to = target,
                timestamp = System.currentTimeMillis()
            )

        historyStore.append(
            knowledge,
            historyEntry
        )

        try {
            stateStore.setState(
                knowledge,
                target
            )
        } catch (error: Throwable) {
            try {
                historyStore.removeLast(
                    knowledge,
                    historyEntry
                )
            } catch (rollbackError: Throwable) {
                error.addSuppressed(
                    rollbackError
                )
            }

            throw error
        }

            true
        }
    }
}
