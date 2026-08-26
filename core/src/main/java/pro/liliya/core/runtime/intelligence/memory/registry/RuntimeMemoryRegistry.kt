package pro.liliya.core.runtime.intelligence.memory.registry

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability

interface RuntimeMemoryRegistry {

    fun register(
        type: RuntimeMemoryType,
        provider: RuntimeMemoryProvider,
        capabilities: Set<RuntimeMemoryCapability>
    )

    fun unregister(
        type: RuntimeMemoryType
    )

    fun registration(
        type: RuntimeMemoryType
    ): RuntimeMemoryRegistration?

    fun provider(
        type: RuntimeMemoryType
    ): RuntimeMemoryProvider?

    fun capabilities(
        type: RuntimeMemoryType
    ): Set<RuntimeMemoryCapability>
}
