package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.graph.DefaultRuntimeKnowledgeGraphBuilder
import pro.liliya.core.runtime.intelligence.knowledge.graph.store.DefaultRuntimeKnowledgeGraphStore

class DefaultRuntimeKnowledgeGraphStoreContractTest {

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
    fun store_keeps_edge_order() {

        val store = DefaultRuntimeKnowledgeGraphStore()

        val first =
            DefaultRuntimeKnowledgeGraphBuilder()
                .connect(
                    source = knowledge("first"),
                    target = knowledge("second"),
                    type = RuntimeKnowledgeAssociationType.RELATED
                )

        val second =
            DefaultRuntimeKnowledgeGraphBuilder()
                .connect(
                    source = knowledge("third"),
                    target = knowledge("fourth"),
                    type = RuntimeKnowledgeAssociationType.CAUSES
                )

        store.append(first)
        store.append(second)

        assertEquals(
            listOf(first, second),
            store.edges()
        )
    }

    @Test
    fun store_does_not_expose_internal_storage() {

        val store = DefaultRuntimeKnowledgeGraphStore()

        store.append(
            DefaultRuntimeKnowledgeGraphBuilder()
                .connect(
                    source = knowledge("source"),
                    target = knowledge("target"),
                    type = RuntimeKnowledgeAssociationType.RELATED
                )
        )

        val first = store.edges()
        val second = store.edges()

        assertNotSame(
            first,
            second
        )
    }
}
