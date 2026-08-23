package pro.liliya.core.runtime.intelligence.knowledge.revision

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeRevision(
    val previous: RuntimeKnowledge,
    val updated: RuntimeKnowledge,
    val reason: RuntimeKnowledgeRevisionReason,
    val createdAt: Long
)
