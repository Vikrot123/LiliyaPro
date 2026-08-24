package pro.liliya.core.runtime.intelligence.knowledge.retrieval.lifecycle

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecycleFilter {

    fun filter(
        knowledge: List<RuntimeKnowledge>
    ): List<RuntimeKnowledge>
}
