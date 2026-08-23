package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.association.DefaultRuntimeKnowledgeAssociator
import pro.liliya.core.runtime.intelligence.knowledge.association.RuntimeKnowledgeAssociationType
import pro.liliya.core.runtime.intelligence.knowledge.association.store.DefaultRuntimeKnowledgeAssociationStore

class DefaultRuntimeKnowledgeAssociationStoreContractTest {

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
    fun store_keeps_association_order() {

        val store = DefaultRuntimeKnowledgeAssociationStore()

        val first =
            DefaultRuntimeKnowledgeAssociator()
                .associate(
                    knowledge("first"),
                    knowledge("second"),
                    RuntimeKnowledgeAssociationType.RELATED
                )

        val second =
            DefaultRuntimeKnowledgeAssociator()
                .associate(
                    knowledge("third"),
                    knowledge("fourth"),
                    RuntimeKnowledgeAssociationType.CAUSES
                )

        store.append(first)
        store.append(second)

        assertEquals(
            listOf(first, second),
            store.associations()
        )
    }

    @Test
    fun store_does_not_expose_internal_storage() {

        val store = DefaultRuntimeKnowledgeAssociationStore()

        store.append(
            DefaultRuntimeKnowledgeAssociator()
                .associate(
                    knowledge("source"),
                    knowledge("target"),
                    RuntimeKnowledgeAssociationType.RELATED
                )
        )

        val first = store.associations()
        val second = store.associations()

        assertNotSame(
            first,
            second
        )
    }
}
