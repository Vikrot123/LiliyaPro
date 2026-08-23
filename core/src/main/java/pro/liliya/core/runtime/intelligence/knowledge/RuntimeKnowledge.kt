package pro.liliya.core.runtime.intelligence.knowledge

data class RuntimeKnowledge(
    val statement: String,
    val confidence: Double,
    val source: RuntimeKnowledgeSource,
    val createdAt: Long
)
