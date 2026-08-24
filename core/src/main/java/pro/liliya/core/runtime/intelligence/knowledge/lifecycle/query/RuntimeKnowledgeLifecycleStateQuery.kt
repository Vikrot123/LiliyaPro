package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

interface RuntimeKnowledgeLifecycleStateQuery {

    fun getState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState?
}
