package pro.liliya.core.runtime.intelligence.knowledge.graph.ranking

import pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQueryResult

data class RuntimeKnowledgeGraphRankingResult(
    val result: RuntimeKnowledgeGraphQueryResult,
    val score: Double,
    val reason: String
)
