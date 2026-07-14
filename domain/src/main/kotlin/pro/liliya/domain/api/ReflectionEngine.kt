package pro.liliya.domain.api

import pro.liliya.domain.models.Episode

interface ReflectionEngine {

    suspend fun reflect(
        episode: Episode
    ): String

}
