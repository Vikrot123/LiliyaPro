package pro.liliya.core.runtime.intelligence.memory.composition

import pro.liliya.core.runtime.intelligence.memory.access.DefaultRuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.access.RuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.installer.DefaultRuntimeMemoryProviderInstaller
import pro.liliya.core.runtime.intelligence.memory.installer.RuntimeMemoryProviderInstaller
import pro.liliya.core.runtime.intelligence.memory.registry.DefaultRuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.memory.service.DefaultRuntimeMemoryService
import pro.liliya.core.runtime.intelligence.memory.service.RuntimeMemoryService
import pro.liliya.core.runtime.intelligence.memory.lifecycle.DefaultRuntimeMemoryLifecycleController
import pro.liliya.core.runtime.intelligence.memory.lifecycle.RuntimeMemoryLifecycleController
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore

class DefaultRuntimeMemoryComposition :
    RuntimeMemoryComposition {

    private val registry: RuntimeMemoryRegistry =
        DefaultRuntimeMemoryRegistry()

    private val access: RuntimeMemoryAccess =
        DefaultRuntimeMemoryAccess(
            registry
        )

    private val service: RuntimeMemoryService =
        DefaultRuntimeMemoryService(
            access
        )

    private val lifecycle: RuntimeMemoryLifecycleController =
        DefaultRuntimeMemoryLifecycleController()

    private val knowledgeLifecycleStateStore:
        RuntimeKnowledgeLifecycleStateStore =
        DefaultRuntimeKnowledgeLifecycleStateStore()

    private val knowledgeMemory: RuntimeKnowledgeMemory =
        DefaultRuntimeKnowledgeMemory(
            lifecycleStateStore = knowledgeLifecycleStateStore
        )

    private val providerInstaller: RuntimeMemoryProviderInstaller =
        DefaultRuntimeMemoryProviderInstaller(
            knowledgeMemory
        )

    init {
        providerInstaller.install(
            registry
        )
    }

    override fun registry(): RuntimeMemoryRegistry {
        return registry
    }

    override fun access(): RuntimeMemoryAccess {
        return access
    }

    override fun service(): RuntimeMemoryService {
        return service
    }

    override fun lifecycle(): RuntimeMemoryLifecycleController {
        return lifecycle
    }

    override fun knowledgeMemory(): RuntimeKnowledgeMemory {
        return knowledgeMemory
    }

    override fun knowledgeLifecycleStateStore():
        RuntimeKnowledgeLifecycleStateStore {
        return knowledgeLifecycleStateStore
    }
}
