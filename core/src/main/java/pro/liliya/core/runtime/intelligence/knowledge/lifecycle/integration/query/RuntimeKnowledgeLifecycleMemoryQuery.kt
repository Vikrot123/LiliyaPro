package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

interface RuntimeKnowledgeLifecycleMemoryQuery {

    fun getState(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleState?
}
