package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.provider

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.RuntimeKnowledgeLifecycleObserverRegistry

interface RuntimeKnowledgeLifecycleObserverProvider {

    fun observerRegistry():
        RuntimeKnowledgeLifecycleObserverRegistry
}
