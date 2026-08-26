package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.DefaultRuntimeKnowledgeAssociator
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociation
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.association.store.DefaultRuntimeKnowledgeAssociationStore
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphEdge
import pro.liliya.core.runtime.intelligence.knowledge.graph.store.RuntimeKnowledgeGraphStore
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory

class DefaultRuntimeKnowledgeMemoryAssociationRollbackOwnershipContractTest {

    @Test
    fun failed_associate_rollback_preserves_preexisting_equal_association() {

        val source =
            RuntimeKnowledge(
                statement = "ownership source",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val target =
            RuntimeKnowledge(
                statement = "ownership target",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 2L
            )

        val associationStore =
            DefaultRuntimeKnowledgeAssociationStore()

        val preexisting =
            DefaultRuntimeKnowledgeAssociator()
                .associate(
                    source,
                    target,
                    RuntimeKnowledgeAssociationType.RELATED
                )

        associationStore.append(
            preexisting
        )

        val graphStore =
            object : RuntimeKnowledgeGraphStore {

                override fun append(
                    edge: RuntimeKnowledgeGraphEdge
                ) {
                    throw IllegalStateException(
                        "graph append failed"
                    )
                }

                override fun edges():
                    List<RuntimeKnowledgeGraphEdge> {
                    return emptyList()
                }
            }

        val memory =
            DefaultRuntimeKnowledgeMemory(
                associationStore = associationStore,
                graphStore = graphStore
            )

        assertFailsWith<IllegalStateException> {
            memory.associate(
                source,
                target,
                RuntimeKnowledgeAssociationType.RELATED
            )
        }

        val remaining =
            associationStore.associations()

        assertEquals(
            1,
            remaining.size,
            "rollback must remove only the failed association append"
        )

        assertEquals(
            preexisting,
            remaining.single(),
            "preexisting equal association must survive rollback"
        )
    }
}
