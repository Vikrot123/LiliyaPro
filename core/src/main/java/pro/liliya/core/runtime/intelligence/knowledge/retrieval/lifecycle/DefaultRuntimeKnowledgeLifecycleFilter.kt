package pro.liliya.core.runtime.intelligence.knowledge.retrieval.lifecycle

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.RuntimeKnowledgeLifecycleStateQuery

class DefaultRuntimeKnowledgeLifecycleFilter(
    private val stateQuery: RuntimeKnowledgeLifecycleStateQuery
) : RuntimeKnowledgeLifecycleFilter {

    override fun filter(
        knowledge: List<RuntimeKnowledge>
    ): List<RuntimeKnowledge> {

        return knowledge.filter { item ->

            when (stateQuery.getState(item)) {
                RuntimeKnowledgeLifecycleState.ARCHIVED ->
                    false

                else ->
                    true
            }
        }
    }
}
