package pro.liliya.core.runtime.intelligence.knowledge.graph.query

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode

interface RuntimeKnowledgeGraphQueryEngine {

    fun query(
        query: RuntimeKnowledgeGraphQuery,
        nodes: List<RuntimeKnowledgeGraphNode>
    ): List<RuntimeKnowledgeGraphQueryResult>
}
