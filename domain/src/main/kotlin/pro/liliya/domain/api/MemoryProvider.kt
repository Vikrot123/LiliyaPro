package pro.liliya.domain.api

import pro.liliya.domain.models.Episode

interface MemoryProvider {

    suspend fun saveEpisode(
        episode: Episode
    )

    suspend fun loadEpisodes(): List<Episode>
}
