package pro.liliya.core.runtime.intelligence.knowledge.association

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeAssociator {

    fun associate(
        source: RuntimeKnowledge,
        target: RuntimeKnowledge,
        type: RuntimeKnowledgeAssociationType
    ): RuntimeKnowledgeAssociation
}
