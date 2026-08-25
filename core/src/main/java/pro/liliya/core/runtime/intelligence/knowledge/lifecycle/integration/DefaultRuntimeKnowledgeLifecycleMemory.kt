package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.DefaultRuntimeKnowledgeLifecycleManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.RuntimeKnowledgeLifecycleTransitionManager

class DefaultRuntimeKnowledgeLifecycleMemory(
    private val stateStore: RuntimeKnowledgeLifecycleStateStore =
        DefaultRuntimeKnowledgeLifecycleStateStore(),
    private val historyStore: RuntimeKnowledgeLifecycleHistoryStore =
        DefaultRuntimeKnowledgeLifecycleHistoryStore(),
    private val transitionManager: RuntimeKnowledgeLifecycleTransitionManager =
        DefaultRuntimeKnowledgeLifecycleTransitionManager(
            DefaultRuntimeKnowledgeLifecycleStateQuery(
                stateStore
            ),
            stateStore,
            historyStore
        ),
    private val knowledgeMemory: RuntimeKnowledgeMemory =
        DefaultRuntimeKnowledgeMemory(
            lifecycleStateStore = stateStore
        )
) : RuntimeKnowledgeLifecycleMemory {

    private val memory = knowledgeMemory

    private val lifecycle =
        DefaultRuntimeKnowledgeLifecycleManager()

    override fun create(
        knowledge: RuntimeKnowledge
    ) {
        memory.remember(
            knowledge
        )

        transitionManager.transition(
            knowledge,
            lifecycle.create(
                knowledge
            ).state
        )
    }

    override fun activate(
        knowledge: RuntimeKnowledge
    ) {
        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )
    }

    override fun revise(
        knowledge: RuntimeKnowledge
    ) {
        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.REVIEW
        )
    }

    override fun archive(
        knowledge: RuntimeKnowledge
    ) {
        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ARCHIVED
        )
    }

    override fun memory(): RuntimeKnowledgeMemory {
        return memory
    }
}
