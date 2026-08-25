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
            return false
        }

        stateStore.setState(
            knowledge,
            target
        )

        historyStore.append(
            knowledge,
            RuntimeKnowledgeLifecycleHistoryEntry(
                from = current,
                to = target,
                timestamp = System.currentTimeMillis()
            )
        )

        return true
    }
}
