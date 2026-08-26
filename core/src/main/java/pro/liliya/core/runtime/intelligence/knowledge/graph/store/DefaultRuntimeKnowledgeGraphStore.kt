package pro.liliya.core.runtime.intelligence.knowledge.graph.store

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphEdge

class DefaultRuntimeKnowledgeGraphStore :
    RuntimeKnowledgeGraphStore {

    private val edges =
        mutableListOf<RuntimeKnowledgeGraphEdge>()

    override fun append(
        edge: RuntimeKnowledgeGraphEdge
    ) {
        synchronized(edges) {
            edges += edge
        }
    }

    override fun edges(): List<RuntimeKnowledgeGraphEdge> {
        return synchronized(edges) {
            edges.toList()
        }
    }
}
