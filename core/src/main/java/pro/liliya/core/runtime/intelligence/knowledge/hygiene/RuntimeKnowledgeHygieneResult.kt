package pro.liliya.core.runtime.intelligence.knowledge.hygiene

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.conflict.RuntimeKnowledgeConflictResolution

data class RuntimeKnowledgeHygieneResult(
    val action: RuntimeKnowledgeHygieneAction,
    val requestedKnowledge: RuntimeKnowledge,
    val retainedKnowledge: RuntimeKnowledge,
    val conflictResolution: RuntimeKnowledgeConflictResolution? = null
)
