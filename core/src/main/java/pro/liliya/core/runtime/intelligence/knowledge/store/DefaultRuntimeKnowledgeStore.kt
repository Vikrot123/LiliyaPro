package pro.liliya.core.runtime.intelligence.knowledge.store

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeStore :
    RuntimeKnowledgeStore {

    private val knowledge =
        mutableListOf<RuntimeKnowledge>()

    override fun append(
        knowledge: RuntimeKnowledge
    ) {
        this.knowledge += knowledge
    }

    override fun knowledge(): List<RuntimeKnowledge> {
        return knowledge.toList()
    }
}
