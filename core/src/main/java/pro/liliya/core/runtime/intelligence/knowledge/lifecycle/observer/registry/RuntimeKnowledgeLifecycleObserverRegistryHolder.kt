package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry

interface RuntimeKnowledgeLifecycleObserverRegistryHolder {

    fun registry(): RuntimeKnowledgeLifecycleObserverRegistry

    fun reset()
}
