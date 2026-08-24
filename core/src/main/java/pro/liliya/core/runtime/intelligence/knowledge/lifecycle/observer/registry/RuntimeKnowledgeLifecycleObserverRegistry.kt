package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

interface RuntimeKnowledgeLifecycleObserverRegistry {

    fun register(
        observer: RuntimeKnowledgeLifecycleObserver
    )

    fun notify(
        result: RuntimeKnowledgeLifecycleServiceResult
    )
}
