package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.holder

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService

class DefaultRuntimeKnowledgeLifecycleServiceHolder(
    private val factory: () -> RuntimeKnowledgeLifecycleService
) : RuntimeKnowledgeLifecycleServiceHolder {

    private var currentService:
        RuntimeKnowledgeLifecycleService = factory()

    override fun service():
        RuntimeKnowledgeLifecycleService {
        return currentService
    }

    override fun reset() {
        currentService = factory()
    }
}
