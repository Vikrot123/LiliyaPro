package pro.liliya.core.memory

import pro.liliya.domain.models.Episode
import pro.liliya.domain.models.SystemEvent
import java.util.UUID

class EpisodeBuilder {

    fun createEpisode(
        events: List<SystemEvent>
    ): Episode {

        return Episode(
            id = UUID.randomUUID().toString(),
            events = events
        )
    }
}
