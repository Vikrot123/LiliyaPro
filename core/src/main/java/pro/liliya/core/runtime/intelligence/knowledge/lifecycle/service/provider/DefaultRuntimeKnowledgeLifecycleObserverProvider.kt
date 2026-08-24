package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.provider

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.RuntimeKnowledgeLifecycleObserverRegistry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.RuntimeKnowledgeLifecycleObserverRegistryHolder

class DefaultRuntimeKnowledgeLifecycleObserverProvider(
    private val holder:
        RuntimeKnowledgeLifecycleObserverRegistryHolder
) : RuntimeKnowledgeLifecycleObserverProvider {

    override fun observerRegistry():
        RuntimeKnowledgeLifecycleObserverRegistry {

        return holder.registry()
    }
}
