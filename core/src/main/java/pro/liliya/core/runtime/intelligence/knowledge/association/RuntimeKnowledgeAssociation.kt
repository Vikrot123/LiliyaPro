package pro.liliya.core.runtime.intelligence.knowledge.association

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeAssociation(
    val source: RuntimeKnowledge,
    val target: RuntimeKnowledge,
    val type: RuntimeKnowledgeAssociationType,
    val createdAt: Long
)
