package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.provider.DefaultRuntimeKnowledgeLifecycleObserverProvider

class DefaultRuntimeKnowledgeLifecycleObserverProviderContractTest {

    @Test
    fun provider_returns_registry_from_holder() {

        val holder =
            DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder()

        val provider =
            DefaultRuntimeKnowledgeLifecycleObserverProvider(
                holder
            )

        assertSame(
            holder.registry(),
            provider.observerRegistry()
        )
    }
    @Test
    fun provider_follows_holder_registry_after_reset() {
        val holder =
            DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder()

        val provider =
            DefaultRuntimeKnowledgeLifecycleObserverProvider(holder)

        val beforeReset = provider.observerRegistry()

        holder.reset()

        val afterReset = provider.observerRegistry()

        assertSame(
            holder.registry(),
            afterReset
        )
        kotlin.test.assertNotSame(
            beforeReset,
            afterReset
        )
    }

}
