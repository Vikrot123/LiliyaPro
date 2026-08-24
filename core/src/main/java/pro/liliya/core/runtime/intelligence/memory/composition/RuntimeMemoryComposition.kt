package pro.liliya.core.runtime.intelligence.memory.composition

import pro.liliya.core.runtime.intelligence.memory.access.RuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry
import pro.liliya.core.runtime.intelligence.memory.service.RuntimeMemoryService
import pro.liliya.core.runtime.intelligence.memory.lifecycle.RuntimeMemoryLifecycleController

interface RuntimeMemoryComposition {

    fun registry(): RuntimeMemoryRegistry

    fun access(): RuntimeMemoryAccess

    fun service(): RuntimeMemoryService

    fun lifecycle(): RuntimeMemoryLifecycleController
}
