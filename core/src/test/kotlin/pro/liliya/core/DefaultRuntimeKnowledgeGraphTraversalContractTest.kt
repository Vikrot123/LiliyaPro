package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.DefaultRuntimeKnowledgeGraphBuilder
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphNode
import pro.liliya.core.runtime.intelligence.knowledge.graph.traversal.DefaultRuntimeKnowledgeGraphTraversal

class DefaultRuntimeKnowledgeGraphTraversalContractTest {

    private fun knowledge(
        statement: String
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun traversal_returns_target_neighbors() {

        val source = knowledge("runtime state")
        val target = knowledge("runtime recovery")

        val edge =
            DefaultRuntimeKnowledgeGraphBuilder()
                .connect(
                    source = source,
                    target = target,
                    type = RuntimeKnowledgeAssociationType.RELATED
                )

        val result =
            DefaultRuntimeKnowledgeGraphTraversal()
                .neighbors(
                    node = edge.source,
                    edges = listOf(edge)
                )

        assertEquals(
            listOf(edge.target),
            result
        )
    }

    @Test
    fun traversal_returns_empty_for_isolated_node() {

        val node =
            RuntimeKnowledgeGraphNode(
                knowledge = knowledge("isolated"),
                createdAt = 1L
            )

        val result =
            DefaultRuntimeKnowledgeGraphTraversal()
                .neighbors(
                    node = node,
                    edges = emptyList()
                )

        assertEquals(
            emptyList(),
            result
        )
    }
}
