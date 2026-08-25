package pro.liliya.core

import kotlin.test.Test
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.DefaultRuntimeKnowledgeLifecycleComposition

class DefaultRuntimeKnowledgeLifecycleCompositionContractTest {

    @Test
    fun composition_returns_same_service_instance() {
        val composition =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val first =
            composition.lifecycleService()

        val second =
            composition.lifecycleService()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun composition_reset_creates_new_service_instance() {
        val composition =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val first =
            composition.lifecycleService()

        composition.reset()

        val second =
            composition.lifecycleService()

        assertNotSame(
            first,
            second
        )
    }
    @Test
    fun composition_reset_preserves_memory_and_history_query_identity() {
        val composition = DefaultRuntimeKnowledgeLifecycleComposition(
            DefaultRuntimeKnowledgeMemory(),
            DefaultRuntimeKnowledgeLifecycleStateStore()
        )

        val beforeMemory = composition.lifecycleMemory()
        val beforeHistoryQuery = composition.lifecycleHistoryQuery()

        composition.reset()

        val afterMemory = composition.lifecycleMemory()
        val afterHistoryQuery = composition.lifecycleHistoryQuery()

        assertSame(beforeMemory, afterMemory)
        assertSame(beforeHistoryQuery, afterHistoryQuery)
    }

}
