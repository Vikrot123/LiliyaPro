package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration.RuntimeKnowledgeLifecycleMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.maintenance.RuntimeKnowledgeMaintenanceService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.RuntimeKnowledgeSupersessionHistory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.query.RuntimeKnowledgeSupersessionQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.query.RuntimeKnowledgeLifecycleHistoryQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver

interface RuntimeKnowledgeLifecycleComposition {

    fun lifecycleService():
        RuntimeKnowledgeLifecycleService

    fun lifecycleMemory():
        RuntimeKnowledgeLifecycleMemory

    fun maintenanceService():
        RuntimeKnowledgeMaintenanceService

    fun supersessionHistory():
        RuntimeKnowledgeSupersessionHistory

    fun supersessionQuery():
        RuntimeKnowledgeSupersessionQuery

    fun lifecycleHistoryQuery():
        RuntimeKnowledgeLifecycleHistoryQuery

    fun registerLifecycleObserver(
        observer: RuntimeKnowledgeLifecycleObserver
    )

    fun unregisterLifecycleObserver(
        observer: RuntimeKnowledgeLifecycleObserver
    )

    fun reset()
}
