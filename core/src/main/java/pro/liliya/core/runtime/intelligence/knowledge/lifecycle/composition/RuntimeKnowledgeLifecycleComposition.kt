package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService

interface RuntimeKnowledgeLifecycleComposition {

    fun lifecycleService():
        RuntimeKnowledgeLifecycleService

    fun reset()
}
