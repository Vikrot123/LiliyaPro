package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

interface RuntimeKnowledgeLifecycleStateStore {

    fun setState(
        knowledge: RuntimeKnowledge,
        state: RuntimeKnowledgeLifecycleState
    )

    fun getState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState?
}
