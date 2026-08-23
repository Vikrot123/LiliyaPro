package pro.liliya.core.runtime.intelligence.knowledge.conflict

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeConflictResolution(
    val resolved: Boolean,
    val selectedKnowledge: RuntimeKnowledge?,
    val reason: String
)
