package pro.liliya.core.runtime.intelligence.knowledge.graph.traversal

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphEdge
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode

interface RuntimeKnowledgeGraphTraversal {

    fun neighbors(
        node: RuntimeKnowledgeGraphNode,
        edges: List<RuntimeKnowledgeGraphEdge>
    ): List<RuntimeKnowledgeGraphNode>
}
