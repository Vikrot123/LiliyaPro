package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.DefaultRuntimeKnowledgeLifecycleManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleMemory :
    RuntimeKnowledgeLifecycleMemory {

    private val memory =
        DefaultRuntimeKnowledgeMemory()

    private val lifecycle =
        DefaultRuntimeKnowledgeLifecycleManager()

    private val stateStore =
        DefaultRuntimeKnowledgeLifecycleStateStore()

    override fun create(
        knowledge: RuntimeKnowledge
    ) {
        memory.remember(
            knowledge
        )

        stateStore.setState(
            knowledge,
            lifecycle.create(
                knowledge
            ).state
        )
    }

    override fun activate(
        knowledge: RuntimeKnowledge
    ) {
        stateStore.setState(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )
    }

    override fun revise(
        knowledge: RuntimeKnowledge
    ) {
        stateStore.setState(
            knowledge,
            RuntimeKnowledgeLifecycleState.REVIEW
        )
    }

    override fun archive(
        knowledge: RuntimeKnowledge
    ) {
        stateStore.setState(
            knowledge,
            RuntimeKnowledgeLifecycleState.ARCHIVED
        )
    }

    override fun memory(): RuntimeKnowledgeMemory {
        return memory
    }
}
