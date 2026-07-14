package pro.liliya.core.memory

import pro.liliya.domain.api.EventStore
import pro.liliya.domain.api.MemoryProvider
import pro.liliya.domain.models.Episode
import java.util.UUID

class MemoryConsolidator(
    private val eventStore: EventStore,
    private val memoryProvider: MemoryProvider
) {

    suspend fun consolidate() {

        val events = eventStore.getEvents()

        if (events.isEmpty()) {
            return
        }

        val episode = Episode(
            id = UUID.randomUUID().toString(),
            events = events
        )

        memoryProvider.saveEpisode(
            episode
        )

        eventStore.clear()
    }
}
