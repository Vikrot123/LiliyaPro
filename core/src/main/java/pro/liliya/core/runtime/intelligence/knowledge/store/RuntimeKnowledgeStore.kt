package pro.liliya.core.runtime.intelligence.knowledge.store

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeStore {

    fun append(
        knowledge: RuntimeKnowledge
    )

    fun removeLast(
        knowledge: RuntimeKnowledge
    )

    fun knowledge(): List<RuntimeKnowledge>
}
