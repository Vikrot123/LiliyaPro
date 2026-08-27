package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeSupersessionRecord(
    val previousKnowledge: RuntimeKnowledge,
    val replacementKnowledge: RuntimeKnowledge,
    val recordedAt: Long
)
