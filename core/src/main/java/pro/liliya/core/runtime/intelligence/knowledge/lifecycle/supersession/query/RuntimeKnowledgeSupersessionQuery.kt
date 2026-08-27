package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeSupersessionQuery {

    fun trace(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeSupersessionTrace

    fun currentKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledge

    fun traceTo(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeSupersessionTrace
}
