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
}
