package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

interface RuntimeKnowledgeLifecycleTransitionManager {

    fun transition(
        knowledge: RuntimeKnowledge,
        target: RuntimeKnowledgeLifecycleState
    ): Boolean
}
