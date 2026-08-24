package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeKnowledgeLifecycleStateQuery(
    private val stateStore: RuntimeKnowledgeLifecycleStateStore
) : RuntimeKnowledgeLifecycleStateQuery {

    override fun getState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState? {
        return stateStore.getState(
            knowledge
        )
    }
}
