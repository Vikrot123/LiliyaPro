package pro.liliya.core.runtime.intelligence.knowledge.graph.store

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphEdge

interface RuntimeKnowledgeGraphStore {

    fun append(
        edge: RuntimeKnowledgeGraphEdge
    )

    fun edges(): List<RuntimeKnowledgeGraphEdge>
}
