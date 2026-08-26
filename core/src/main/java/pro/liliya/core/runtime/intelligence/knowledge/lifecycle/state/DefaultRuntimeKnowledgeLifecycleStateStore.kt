package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class DefaultRuntimeKnowledgeLifecycleStateStore :
    RuntimeKnowledgeLifecycleStateStore {

    private val states =
        mutableMapOf<
            RuntimeKnowledge,
            RuntimeKnowledgeLifecycleState
        >()

    override fun setState(
        knowledge: RuntimeKnowledge,
        state: RuntimeKnowledgeLifecycleState
    ) {
        synchronized(states) {
            states[knowledge] = state
        }
    }

    override fun getState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState? {
        return synchronized(states) {
            states[knowledge]
        }
    }
}
