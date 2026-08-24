package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

class DefaultRuntimeKnowledgeLifecycleObserverRegistry :
    RuntimeKnowledgeLifecycleObserverRegistry {

    private val observers =
        mutableListOf<RuntimeKnowledgeLifecycleObserver>()

    override fun register(
        observer: RuntimeKnowledgeLifecycleObserver
    ) {
        if (!observers.contains(observer)) {
            observers += observer
        }
    }

    override fun notify(
        result: RuntimeKnowledgeLifecycleServiceResult
    ) {
        observers.forEach { observer ->
            observer.onProcessed(result)
        }
    }
}
