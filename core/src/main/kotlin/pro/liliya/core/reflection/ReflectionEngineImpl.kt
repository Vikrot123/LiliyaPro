package pro.liliya.core.reflection

import pro.liliya.domain.api.ReflectionEngine
import pro.liliya.domain.models.Episode

class ReflectionEngineImpl : ReflectionEngine {

    override suspend fun reflect(
        episode: Episode
    ): String {

        val eventCount = episode.events.size

        return when {
            eventCount == 0 ->
                "No experience to reflect"

            eventCount == 1 ->
                "Single event processed"

            else ->
                "Experience analyzed: $eventCount events"
        }
    }
}
