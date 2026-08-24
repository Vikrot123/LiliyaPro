package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

data class RuntimeKnowledgeLifecycleHistoryEntry(
    val from: RuntimeKnowledgeLifecycleState?,
    val to: RuntimeKnowledgeLifecycleState,
    val timestamp: Long
)
