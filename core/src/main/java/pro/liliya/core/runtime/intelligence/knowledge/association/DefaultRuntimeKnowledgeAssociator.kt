package pro.liliya.core.runtime.intelligence.knowledge.association

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

class DefaultRuntimeKnowledgeAssociator :
    RuntimeKnowledgeAssociator {

    override fun associate(
        source: RuntimeKnowledge,
        target: RuntimeKnowledge,
        type: RuntimeKnowledgeAssociationType
    ): RuntimeKnowledgeAssociation {

        return RuntimeKnowledgeAssociation(
            source = source,
            target = target,
            type = type,
            createdAt = System.currentTimeMillis()
        )
    }
}
