package pro.liliya.core.runtime.intelligence.knowledge.graph

import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType

data class RuntimeKnowledgeGraphEdge(
    val source: RuntimeKnowledgeGraphNode,
    val target: RuntimeKnowledgeGraphNode,
    val type: RuntimeKnowledgeAssociationType,
    val createdAt: Long
)
