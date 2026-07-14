package pro.liliya.core.memory

import pro.liliya.domain.memory.MemoryItem
import pro.liliya.domain.memory.MemoryRepository
import java.util.UUID

class MemoryCoordinator(
    private val repository: MemoryRepository
) {

    suspend fun remember(
        content: String,
        importance: Float = 0.5f
    ) {

        val memory = MemoryItem(
            id = UUID.randomUUID().toString(),
            content = content,
            timestamp = System.currentTimeMillis(),
            importance = importance
        )

        repository.save(memory)
    }


    suspend fun recall(
        query: String
    ): List<MemoryItem> {

        return repository.search(query)
    }
}
