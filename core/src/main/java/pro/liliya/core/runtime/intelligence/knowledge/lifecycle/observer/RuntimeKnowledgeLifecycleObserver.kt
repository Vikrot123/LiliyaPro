package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

interface RuntimeKnowledgeLifecycleObserver {

    fun onProcessed(
        result: RuntimeKnowledgeLifecycleServiceResult
    )
}
