package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.store.DefaultRuntimeKnowledgeStore

class DefaultRuntimeKnowledgeStoreContractTest {

    private fun knowledge(
        text: String
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = text,
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun store_keeps_knowledge_order() {

        val store = DefaultRuntimeKnowledgeStore()

        val first = knowledge("first")
        val second = knowledge("second")

        store.append(first)
        store.append(second)

        assertEquals(
            listOf(first, second),
            store.knowledge()
        )
    }

    @Test
    fun store_does_not_expose_internal_storage() {

        val store = DefaultRuntimeKnowledgeStore()

        store.append(
            knowledge("state")
        )

        val first = store.knowledge()
        val second = store.knowledge()

        assertNotSame(
            first,
            second
        )
    }
}
