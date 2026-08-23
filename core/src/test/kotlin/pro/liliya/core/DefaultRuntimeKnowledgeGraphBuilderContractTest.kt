package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.DefaultRuntimeKnowledgeGraphBuilder

class DefaultRuntimeKnowledgeGraphBuilderContractTest {

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
    fun graph_edge_keeps_source_target_and_type() {

        val source = knowledge("runtime state")
        val target = knowledge("runtime recovery")

        val edge =
            DefaultRuntimeKnowledgeGraphBuilder()
                .connect(
                    source = source,
                    target = target,
                    type = RuntimeKnowledgeAssociationType.RELATED
                )

        assertEquals(
            source,
            edge.source.knowledge
        )

        assertEquals(
            target,
            edge.target.knowledge
        )

        assertEquals(
            RuntimeKnowledgeAssociationType.RELATED,
            edge.type
        )
    }
}
