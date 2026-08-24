package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.RuntimeKnowledgeLifecycleStateQuery

class DefaultRuntimeKnowledgeLifecycleMemoryQuery(
    private val stateQuery: RuntimeKnowledgeLifecycleStateQuery
) : RuntimeKnowledgeLifecycleMemoryQuery {

    override fun getState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState? {
        return stateQuery.getState(
            knowledge
        )
    }
}
