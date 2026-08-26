package pro.liliya.core.runtime.intelligence.memory.registry

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability

class DefaultRuntimeMemoryRegistry :
    RuntimeMemoryRegistry {

    private val memories =
        mutableMapOf<RuntimeMemoryType, RuntimeMemoryRegistration>()

    override fun register(
        type: RuntimeMemoryType,
        provider: RuntimeMemoryProvider,
        capabilities: Set<RuntimeMemoryCapability>
    ) {
        synchronized(memories) {
            if (!memories.containsKey(type)) {
                memories[type] =
                    RuntimeMemoryRegistration(
                        provider = provider,
                        capabilities = capabilities.toSet()
                    )
            }
        }
    }

    override fun unregister(
        type: RuntimeMemoryType
    ) {
        synchronized(memories) {
            memories.remove(type)
        }
    }

    override fun registration(
        type: RuntimeMemoryType
    ): RuntimeMemoryRegistration? {
        return synchronized(memories) {
            memories[type]
        }
    }

    override fun provider(
        type: RuntimeMemoryType
    ): RuntimeMemoryProvider? {
        return registration(type)
            ?.provider
    }

    override fun capabilities(
        type: RuntimeMemoryType
    ): Set<RuntimeMemoryCapability> {
        return registration(type)
            ?.capabilities
            ?: emptySet()
    }
}
