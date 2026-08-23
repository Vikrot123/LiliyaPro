package pro.liliya.core.runtime.intelligence.knowledge.graph.traversal

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphEdge
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode

class DefaultRuntimeKnowledgeGraphTraversal :
    RuntimeKnowledgeGraphTraversal {

    override fun neighbors(
        node: RuntimeKnowledgeGraphNode,
        edges: List<RuntimeKnowledgeGraphEdge>
    ): List<RuntimeKnowledgeGraphNode> {

        return edges
            .filter {
                it.source == node
            }
            .map {
                it.target
            }
    }
}
