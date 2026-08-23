package pro.liliya.core.runtime.intelligence.knowledge.lifecycle

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeLifecycle(
    val knowledge: RuntimeKnowledge,
    val state: RuntimeKnowledgeLifecycleState,
    val updatedAt: Long
)
