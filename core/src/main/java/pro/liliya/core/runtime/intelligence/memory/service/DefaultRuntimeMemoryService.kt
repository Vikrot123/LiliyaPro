package pro.liliya.core.runtime.intelligence.memory.service

import pro.liliya.core.runtime.intelligence.memory.access.RuntimeMemoryAccess
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType

class DefaultRuntimeMemoryService(
    private val access: RuntimeMemoryAccess
) : RuntimeMemoryService {

    override fun store(
        type: RuntimeMemoryType,
        entry: RuntimeMemoryEntry
    ) {
        access.store(
            type,
            entry
        )
    }

    override fun search(
        type: RuntimeMemoryType,
        query: String
    ): List<RuntimeMemoryEntry> {
        return access.search(
            type,
            query
        )
    }
}
