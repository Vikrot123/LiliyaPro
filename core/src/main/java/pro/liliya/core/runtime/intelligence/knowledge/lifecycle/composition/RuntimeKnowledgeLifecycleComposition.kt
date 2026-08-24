package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.RuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.RuntimeKnowledgeLifecycleHistoryQuery

interface RuntimeKnowledgeLifecycleComposition {

    fun lifecycleService():
        RuntimeKnowledgeLifecycleService

    fun lifecycleMemory():
        RuntimeKnowledgeLifecycleMemory

    fun lifecycleHistoryQuery():
        RuntimeKnowledgeLifecycleHistoryQuery

    fun reset()
}
