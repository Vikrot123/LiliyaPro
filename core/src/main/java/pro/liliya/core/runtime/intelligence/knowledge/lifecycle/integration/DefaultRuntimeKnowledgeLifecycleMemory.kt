package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.DefaultRuntimeKnowledgeLifecycleManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class DefaultRuntimeKnowledgeLifecycleMemory :
    RuntimeKnowledgeLifecycleMemory {

    private val memory =
        DefaultRuntimeKnowledgeMemory()

    private val lifecycle =
        DefaultRuntimeKnowledgeLifecycleManager()

    private val states =
        mutableMapOf<RuntimeKnowledge, RuntimeKnowledgeLifecycleState>()

    override fun create(
        knowledge: RuntimeKnowledge
    ) {
        memory.remember(
            knowledge
        )

        states[knowledge] =
            lifecycle.create(
                knowledge
            ).state
    }

    override fun activate(
        knowledge: RuntimeKnowledge
    ) {
        states[knowledge] =
            RuntimeKnowledgeLifecycleState.ACTIVE
    }

    override fun revise(
        knowledge: RuntimeKnowledge
    ) {
        states[knowledge] =
            RuntimeKnowledgeLifecycleState.REVIEW
    }

    override fun archive(
        knowledge: RuntimeKnowledge
    ) {
        states[knowledge] =
            RuntimeKnowledgeLifecycleState.ARCHIVED
    }

    override fun memory(): RuntimeKnowledgeMemory {
        return memory
    }
}
