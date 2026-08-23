package pro.liliya.core.runtime.intelligence.knowledge.conflict

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeConflict(
    val first: RuntimeKnowledge,
    val second: RuntimeKnowledge,
    val type: RuntimeKnowledgeConflictType,
    val createdAt: Long
)
