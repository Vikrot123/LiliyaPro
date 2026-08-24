package pro.liliya.core.runtime.intelligence.memory.composition

import pro.liliya.core.runtime.intelligence.memory.access.RuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry

interface RuntimeMemoryComposition {

    fun registry(): RuntimeMemoryRegistry

    fun access(): RuntimeMemoryAccess
}
