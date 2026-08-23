package pro.liliya.core.runtime.intelligence.knowledge.priority

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgePriority(
    val knowledge: RuntimeKnowledge,
    val level: RuntimeKnowledgePriorityLevel,
    val score: Double,
    val reason: String
)
