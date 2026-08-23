package pro.liliya.core.runtime.intelligence.knowledge.graph.ranking

import pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQueryResult

interface RuntimeKnowledgeGraphRanker {

    fun rank(
        results: List<RuntimeKnowledgeGraphQueryResult>
    ): List<RuntimeKnowledgeGraphRankingResult>
}
