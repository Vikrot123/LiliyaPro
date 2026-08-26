package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociation
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.association.store.RuntimeKnowledgeAssociationStore
import pro.liliya.core.runtime.intelligence.knowledge.graph.RuntimeKnowledgeGraphEdge
import pro.liliya.core.runtime.intelligence.knowledge.graph.store.RuntimeKnowledgeGraphStore
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory

class DefaultRuntimeKnowledgeMemoryAssociationAtomicityContractTest {

    @Test
    fun failed_graph_append_must_not_leave_association_recorded() {

        val recordedAssociations =
            mutableListOf<RuntimeKnowledgeAssociation>()

        val associationStore =
            object : RuntimeKnowledgeAssociationStore {

                override fun append(
                    association: RuntimeKnowledgeAssociation
                ) {
                    recordedAssociations += association
                }

                override fun removeLast(
                    association: RuntimeKnowledgeAssociation
                ) {
                    if (recordedAssociations.lastOrNull() == association) {
                        recordedAssociations.removeAt(
                            recordedAssociations.lastIndex
                        )
                    }
                }

                override fun associations():
                    List<RuntimeKnowledgeAssociation> {
                    return recordedAssociations.toList()
                }
            }

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

        val source =
            RuntimeKnowledge(
                statement = "atomic source",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val target =
            RuntimeKnowledge(
                statement = "atomic target",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 2L
            )

        assertFailsWith<IllegalStateException> {
            memory.associate(
                source,
                target,
                RuntimeKnowledgeAssociationType.RELATED
            )
        }

        assertEquals(
            0,
            recordedAssociations.size,
            "failed graph append must not leave association committed"
        )
    }
}
