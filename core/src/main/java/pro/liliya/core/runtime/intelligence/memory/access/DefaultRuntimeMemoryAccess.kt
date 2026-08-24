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
        if (
            !registry
                .capabilities(type)
                .contains(
                    RuntimeMemoryCapability.WRITE
                )
        ) {
            return
        }

        registry
            .provider(type)
            ?.store(entry)
    }


    override fun search(
        type: RuntimeMemoryType,
        query: String
    ): List<RuntimeMemoryEntry> {

        if (
            !registry
                .capabilities(type)
                .contains(
                    RuntimeMemoryCapability.READ
                )
        ) {
            return emptyList()
        }

        return registry
            .provider(type)
            ?.search(query)
            ?: emptyList()
    }
}
