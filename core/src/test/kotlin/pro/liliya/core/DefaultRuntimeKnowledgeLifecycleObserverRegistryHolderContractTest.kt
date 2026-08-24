package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder

class DefaultRuntimeKnowledgeLifecycleObserverRegistryHolderContractTest {

    @Test
    fun holder_returns_same_registry_instance() {

        val holder =
            DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder()

        val first = holder.registry()
        val second = holder.registry()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun holder_reset_creates_new_registry_instance() {

        val holder =
            DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder()

        val first = holder.registry()

        holder.reset()

        val second = holder.registry()

        assertNotNull(second)

        assertNotSame(
            first,
            second
        )
    }
}
