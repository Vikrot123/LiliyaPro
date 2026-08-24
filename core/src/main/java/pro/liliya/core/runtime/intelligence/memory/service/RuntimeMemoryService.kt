package pro.liliya.core.runtime.intelligence.memory.service

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType

interface RuntimeMemoryService {

    fun store(
        type: RuntimeMemoryType,
        entry: RuntimeMemoryEntry
    )

    fun search(
        type: RuntimeMemoryType,
        query: String
    ): List<RuntimeMemoryEntry>
}
