package pro.liliya.core

import kotlin.test.Test
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.DefaultRuntimeKnowledgeLifecycleComposition

class RuntimeKnowledgeLifecycleCompositionIsolationContractTest {

    @Test
    fun compositions_own_independent_service_instances() {

        val first =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val second =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val firstService =
            first.lifecycleService()

        val secondService =
            second.lifecycleService()

        assertNotSame(
            firstService,
            secondService
        )
    }

    @Test
    fun reset_does_not_replace_other_composition_service() {

        val first =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val second =
            DefaultRuntimeKnowledgeLifecycleComposition(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val secondService =
            second.lifecycleService()

        first.reset()

        assertSame(
            secondService,
            second.lifecycleService()
        )
    }
}
