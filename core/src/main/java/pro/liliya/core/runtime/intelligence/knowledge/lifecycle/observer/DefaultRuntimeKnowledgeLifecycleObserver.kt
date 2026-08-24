package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

class DefaultRuntimeKnowledgeLifecycleObserver :
    RuntimeKnowledgeLifecycleObserver {

    private var lastResult:
        RuntimeKnowledgeLifecycleServiceResult? = null

    override fun onProcessed(
        result: RuntimeKnowledgeLifecycleServiceResult
    ) {
        lastResult = result
    }

    fun lastProcessedResult():
        RuntimeKnowledgeLifecycleServiceResult? {
        return lastResult
    }
}
