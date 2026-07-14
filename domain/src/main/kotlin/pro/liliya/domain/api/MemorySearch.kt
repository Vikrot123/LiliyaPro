package pro.liliya.domain.api

import pro.liliya.domain.models.Episode

interface MemorySearch {

    suspend fun search(
        query: String
    ): List<Episode>

}
