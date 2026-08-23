package pro.liliya.core.runtime.intelligence.knowledge.graph.ranking

import pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQueryResult

class DefaultRuntimeKnowledgeGraphRanker :
    RuntimeKnowledgeGraphRanker {

    override fun rank(
        results: List<RuntimeKnowledgeGraphQueryResult>
    ): List<RuntimeKnowledgeGraphRankingResult> {

        return results
            .map {
                RuntimeKnowledgeGraphRankingResult(
                    result = it,
                    score = it.node.knowledge.confidence,
                    reason = "Ranked by knowledge confidence"
                )
            }
            .sortedByDescending {
                it.score
            }
    }
}
