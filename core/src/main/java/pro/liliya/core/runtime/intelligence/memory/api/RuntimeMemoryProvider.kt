package pro.liliya.core.runtime.intelligence.memory.api

interface RuntimeMemoryProvider {

    fun store(
        entry: RuntimeMemoryEntry
    )

    fun search(
        query: String
    ): List<RuntimeMemoryEntry>

    fun clear()
}
