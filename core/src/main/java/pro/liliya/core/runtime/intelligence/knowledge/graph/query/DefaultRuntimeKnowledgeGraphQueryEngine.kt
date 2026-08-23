package pro.liliya.core.runtime.intelligence.knowledge.graph.query

import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode

class DefaultRuntimeKnowledgeGraphQueryEngine :
    RuntimeKnowledgeGraphQueryEngine {

    override fun query(
        query: RuntimeKnowledgeGraphQuery,
        nodes: List<RuntimeKnowledgeGraphNode>
    ): List<RuntimeKnowledgeGraphQueryResult> {

        return nodes
            .filter {
                it.knowledge.statement.contains(
                    query.text,
                    ignoreCase = true
                )
            }
            .map {
                RuntimeKnowledgeGraphQueryResult(
                    node = it,
                    relevance = 1.0,
                    reason = "Knowledge statement matches graph query"
                )
            }
    }
}
