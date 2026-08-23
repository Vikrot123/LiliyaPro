package pro.liliya.core.runtime.intelligence.knowledge.retrieval

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

data class RuntimeKnowledgeRetrievalResult(
    val knowledge: RuntimeKnowledge,
    val relevance: Double,
    val reason: String
)
