package pro.liliya.core.runtime.intelligence.knowledge.lifecycle

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecycleManager {

    fun create(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycle

    fun transition(
        lifecycle: RuntimeKnowledgeLifecycle,
        state: RuntimeKnowledgeLifecycleState
    ): RuntimeKnowledgeLifecycle
}
