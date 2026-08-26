package pro.liliya.core.runtime.intelligence.memory.access

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability
import pro.liliya.core.runtime.intelligence.memory.registry.RuntimeMemoryRegistry

class DefaultRuntimeMemoryAccess(
    private val registry: RuntimeMemoryRegistry
) : RuntimeMemoryAccess {

    override fun store(
        type: RuntimeMemoryType,
        entry: RuntimeMemoryEntry
    ) {
        val registration =
            registry.registration(type)
                ?: return

        if (
            !registration.capabilities.contains(
                RuntimeMemoryCapability.WRITE
            )
        ) {
            return
        }

        registration.provider.store(
            entry
        )
    }

    override fun search(
        type: RuntimeMemoryType,
        query: String
    ): List<RuntimeMemoryEntry> {

        val registration =
            registry.registration(type)
                ?: return emptyList()

        if (
            !registration.capabilities.contains(
                RuntimeMemoryCapability.READ
            )
        ) {
            return emptyList()
        }

        return registration.provider.search(
            query
        )
    }
}
