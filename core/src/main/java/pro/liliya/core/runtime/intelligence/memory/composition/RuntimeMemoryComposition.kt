package pro.liliya.core.runtime.intelligence.memory.composition

import pro.liliya.core.runtime.intelligence.memory.access.RuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.memory.service.RuntimeMemoryService
import pro.liliya.core.runtime.intelligence.memory.lifecycle.RuntimeMemoryLifecycleController
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore

interface RuntimeMemoryComposition {

    fun registry(): RuntimeMemoryRegistry

    fun access(): RuntimeMemoryAccess

    fun service(): RuntimeMemoryService

    fun lifecycle(): RuntimeMemoryLifecycleController
    fun knowledgeMemory(): RuntimeKnowledgeMemory
    fun knowledgeLifecycleStateStore(): RuntimeKnowledgeLifecycleStateStore
}
