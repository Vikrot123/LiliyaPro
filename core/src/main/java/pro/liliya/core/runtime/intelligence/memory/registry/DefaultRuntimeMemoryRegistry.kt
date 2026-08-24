package pro.liliya.core.runtime.intelligence.memory.registry

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability

class DefaultRuntimeMemoryRegistry :
    RuntimeMemoryRegistry {

    private data class Registration(
        val provider: RuntimeMemoryProvider,
        val capabilities: Set<RuntimeMemoryCapability>
    )

    private val memories =
        mutableMapOf<RuntimeMemoryType, Registration>()

    override fun register(
        type: RuntimeMemoryType,
        provider: RuntimeMemoryProvider,
        capabilities: Set<RuntimeMemoryCapability>
    ) {
        if (!memories.containsKey(type)) {
            memories[type] =
                Registration(
                    provider,
                    capabilities
                )
        }
    }

    override fun unregister(
        type: RuntimeMemoryType
    ) {
        memories.remove(type)
    }

    override fun provider(
        type: RuntimeMemoryType
    ): RuntimeMemoryProvider? {
        return memories[type]?.provider
    }

    override fun capabilities(
        type: RuntimeMemoryType
    ): Set<RuntimeMemoryCapability> {
        return memories[type]?.capabilities
            ?: emptySet()
    }
}
