package pro.liliya.core.runtime.intelligence.knowledge.lifecycle

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeLifecycleManager :
    RuntimeKnowledgeLifecycleManager {

    override fun create(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycle {

        return RuntimeKnowledgeLifecycle(
            knowledge = knowledge,
            state = RuntimeKnowledgeLifecycleState.ACTIVE,
            updatedAt = System.currentTimeMillis()
        )
    }

    override fun transition(
        lifecycle: RuntimeKnowledgeLifecycle,
        state: RuntimeKnowledgeLifecycleState
    ): RuntimeKnowledgeLifecycle {

        return lifecycle.copy(
            state = state,
            updatedAt = System.currentTimeMillis()
        )
    }
}
