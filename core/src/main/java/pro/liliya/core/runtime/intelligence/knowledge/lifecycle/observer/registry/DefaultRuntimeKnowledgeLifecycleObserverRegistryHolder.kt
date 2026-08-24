package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry

class DefaultRuntimeKnowledgeLifecycleObserverRegistryHolder :
    RuntimeKnowledgeLifecycleObserverRegistryHolder {

    private var currentRegistry:
        RuntimeKnowledgeLifecycleObserverRegistry =
        DefaultRuntimeKnowledgeLifecycleObserverRegistry()

    override fun registry():
        RuntimeKnowledgeLifecycleObserverRegistry {
        return currentRegistry
    }

    override fun reset() {
        currentRegistry =
            DefaultRuntimeKnowledgeLifecycleObserverRegistry()
    }
}
