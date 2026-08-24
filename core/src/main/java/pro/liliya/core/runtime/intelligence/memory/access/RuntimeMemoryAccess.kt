package pro.liliya.core.runtime.intelligence.memory.access

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryEntry
import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryType

interface RuntimeMemoryAccess {

    fun store(
        type: RuntimeMemoryType,
        entry: RuntimeMemoryEntry
    )

    fun search(
        type: RuntimeMemoryType,
        query: String
    ): List<RuntimeMemoryEntry>
}
