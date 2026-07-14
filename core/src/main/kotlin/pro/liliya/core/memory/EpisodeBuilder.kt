package pro.liliya.core.memory

import pro.liliya.domain.api.MemoryProvider
import pro.liliya.domain.models.Episode
import pro.liliya.domain.models.SystemEvent
import java.util.UUID

class EpisodeBuilder(
    private val memoryProvider: MemoryProvider
) {

    suspend fun createEpisode(
        events: List<SystemEvent>
    ): Episode {

        val episode = Episode(
            id = UUID.randomUUID().toString(),
            events = events
        )

        memoryProvider.saveEpisode(episode)

        return episode
    }


    suspend fun history(): List<Episode> {
        return memoryProvider.loadEpisodes()
    }
}

