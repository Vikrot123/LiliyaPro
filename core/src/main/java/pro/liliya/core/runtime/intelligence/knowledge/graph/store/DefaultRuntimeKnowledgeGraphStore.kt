package pro.liliya.core.runtime.intelligence.knowledge.graph.store

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphEdge

class DefaultRuntimeKnowledgeGraphStore :
    RuntimeKnowledgeGraphStore {

    private val edges =
        mutableListOf<RuntimeKnowledgeGraphEdge>()

    override fun append(
        edge: RuntimeKnowledgeGraphEdge
    ) {
        edges += edge
    }

    override fun edges():
        List<RuntimeKnowledgeGraphEdge> {
        return edges.toList()
    }
}
