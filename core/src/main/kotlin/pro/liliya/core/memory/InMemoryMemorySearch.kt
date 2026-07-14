package pro.liliya.core.memory

import pro.liliya.domain.api.MemoryProvider
import pro.liliya.domain.api.MemorySearch
import pro.liliya.domain.models.Episode


class InMemoryMemorySearch(
    private val memoryProvider: MemoryProvider
) : MemorySearch {


    override suspend fun search(
        query: String
    ): List<Episode> {

        val episodes = memoryProvider.loadEpisodes()

        if (query.isBlank()) {
            return emptyList()
        }


        return episodes.filter { episode ->

            episode.events.any { event ->

                event.toString()
                    .contains(
                        query,
                        ignoreCase = true
                    )
            }
        }
    }
}
