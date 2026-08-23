package pro.liliya.core.runtime.intelligence.knowledge.graph.query

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode

data class RuntimeKnowledgeGraphQueryResult(
    val node: RuntimeKnowledgeGraphNode,
    val relevance: Double,
    val reason: String
)
