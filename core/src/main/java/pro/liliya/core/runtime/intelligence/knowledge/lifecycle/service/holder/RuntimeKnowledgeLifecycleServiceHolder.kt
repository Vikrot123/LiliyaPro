package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.holder

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService

interface RuntimeKnowledgeLifecycleServiceHolder {

    fun service():
        RuntimeKnowledgeLifecycleService

    fun reset()
}
