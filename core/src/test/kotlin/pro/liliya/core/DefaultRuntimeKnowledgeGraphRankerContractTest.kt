package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode
import pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQueryResult
import pro.liliya.core.runtime.intelligence.knowledge.graph.ranking.DefaultRuntimeKnowledgeGraphRanker

class DefaultRuntimeKnowledgeGraphRankerContractTest {

    private fun result(
        statement: String,
        confidence: Double
    ): RuntimeKnowledgeGraphQueryResult {

        return RuntimeKnowledgeGraphQueryResult(
            node = pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode(
                knowledge = RuntimeKnowledge(
                    statement = statement,
                    confidence = confidence,
                    source = RuntimeKnowledgeSource.CONSOLIDATION,
                    createdAt = 1L
                ),
                createdAt = 1L
            ),
            relevance = 1.0,
            reason = "query match"
        )
    }

    @Test
    fun ranking_orders_by_confidence_descending() {

        val ranked =
            DefaultRuntimeKnowledgeGraphRanker()
                .rank(
                    listOf(
                        result("low", 0.4),
                        result("high", 0.9)
                    )
                )

        assertEquals(
            "high",
            ranked.first().result.node.knowledge.statement
        )
    }
}
