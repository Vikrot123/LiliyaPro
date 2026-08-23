package pro.liliya.core.runtime.intelligence.knowledge.governance

data class RuntimeKnowledgeValidation(
    val accepted: Boolean,
    val reason: String,
    val confidence: Double
)
