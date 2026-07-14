package pro.liliya.core.memory

import pro.liliya.domain.models.Episode
import pro.liliya.domain.memory.MemoryItem

class MemoryService(
    private val memoryCoordinator: MemoryCoordinator,
    private val memoryConsolidator: MemoryConsolidator,
    private val episodeBuilder: EpisodeBuilder
) {

    suspend fun remember(
        content: String,
        importance: Float = 0.5f
    ) {
        memoryCoordinator.remember(
            content = content,
            importance = importance
        )
    }


    suspend fun rememberInteraction(
        input: String,
        response: String
    ) {
        memoryCoordinator.rememberInteraction(
            input = input,
            response = response
        )
    }


    suspend fun recall(
        query: String
    ): List<MemoryItem> {

        return memoryCoordinator.recall(
            query
        )
    }


    suspend fun createEpisode(
        events: List<pro.liliya.domain.models.SystemEvent>
    ): Episode {

        return episodeBuilder.createEpisode(
            events
        )
    }


    suspend fun consolidate() {

        memoryConsolidator.consolidate()
    }
}
