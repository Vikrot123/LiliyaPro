package pro.liliya.core.memory

import pro.liliya.domain.memory.MemoryItem
import pro.liliya.domain.memory.MemoryRepository

class InMemoryMemoryRepository : MemoryRepository {

    private val memories = mutableListOf<MemoryItem>()


    override suspend fun save(memory: MemoryItem) {
        memories.add(memory)
    }


    override suspend fun find(
        id: String
    ): MemoryItem? {

        return memories.firstOrNull {
            it.id == id
        }
    }


    override suspend fun search(
        query: String
    ): List<MemoryItem> {

        return memories.filter {
            it.content.contains(
                query,
                ignoreCase = true
            )
        }
    }


    override suspend fun delete(
        id: String
    ) {

        memories.removeIf {
            it.id == id
        }
    }


    override suspend fun clear() {

        memories.clear()
    }
}
