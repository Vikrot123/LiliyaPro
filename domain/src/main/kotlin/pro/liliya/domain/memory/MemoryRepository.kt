package pro.liliya.domain.memory

interface MemoryRepository {

    suspend fun save(memory: MemoryItem)

    suspend fun find(id: String): MemoryItem?

    suspend fun search(query: String): List<MemoryItem>

    suspend fun delete(id: String)

    suspend fun clear()
}
