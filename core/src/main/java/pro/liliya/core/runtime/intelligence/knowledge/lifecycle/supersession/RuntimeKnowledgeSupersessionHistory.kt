package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeSupersessionHistory {

    fun record(
        previousKnowledge: RuntimeKnowledge,
        replacementKnowledge: RuntimeKnowledge
    ): RuntimeKnowledgeSupersessionRecord

    fun records():
        List<RuntimeKnowledgeSupersessionRecord>

    fun replacementsOf(
        knowledge: RuntimeKnowledge
    ): List<RuntimeKnowledgeSupersessionRecord>

    fun clear()
}
