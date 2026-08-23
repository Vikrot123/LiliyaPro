package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode
import pro.liliya.core.runtime.intelligence.knowledge.graph.query.DefaultRuntimeKnowledgeGraphQueryEngine
import pro.liliya.core.runtime.intelligence.knowledge.graph.query.RuntimeKnowledgeGraphQuery

class DefaultRuntimeKnowledgeGraphQueryEngineContractTest {

    private fun node(
        statement: String
    ): RuntimeKnowledgeGraphNode {
        return RuntimeKnowledgeGraphNode(
            knowledge = RuntimeKnowledge(
                statement = statement,
                confidence = 0.8,
                source = RuntimeKnowledgeSource.CONSOLIDATION,
                createdAt = 1L
            ),
            createdAt = 1L
        )
    }

    @Test
    fun query_returns_matching_graph_nodes() {

        val result =
            DefaultRuntimeKnowledgeGraphQueryEngine()
                .query(
                    query = RuntimeKnowledgeGraphQuery("runtime"),
                    nodes = listOf(
                        node("runtime health"),
                        node("memory state")
                    )
                )

        assertEquals(
            1,
            result.size
        )

        assertEquals(
            "runtime health",
            result.first().node.knowledge.statement
        )
    }

    @Test
    fun unknown_query_returns_empty_result() {

        val result =
            DefaultRuntimeKnowledgeGraphQueryEngine()
                .query(
                    query = RuntimeKnowledgeGraphQuery("unknown"),
                    nodes = listOf(
                        node("runtime health")
                    )
                )

        assertEquals(
            0,
            result.size
        )
    }
}
