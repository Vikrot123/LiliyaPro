package pro.liliya.core.memory

import pro.liliya.domain.models.Episode
import pro.liliya.domain.models.SystemEvent
import pro.liliya.domain.memory.MemoryItem
import pro.liliya.domain.api.MemoryProvider

class MemoryService(
    private val memoryCoordinator: MemoryCoordinator,
    private val memoryConsolidator: MemoryConsolidator,
    private val episodeBuilder: EpisodeBuilder,
    private val memoryProvider: MemoryProvider
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
        events: List<SystemEvent>
    ): Episode {

        val episode =
            episodeBuilder.createEpisode(
                events
            )

        memoryProvider.saveEpisode(
            episode
        )

        return episode
    }


    suspend fun history(): List<Episode> {

        return memoryProvider.loadEpisodes()
    }


    suspend fun consolidate() {

        memoryConsolidator.consolidate()
    }
}
