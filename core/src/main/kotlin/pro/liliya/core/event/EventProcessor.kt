package pro.liliya.core.event

import pro.liliya.domain.api.EventBus
import pro.liliya.domain.api.EventStore
import pro.liliya.domain.models.SystemEvent

class EventProcessor(
    private val eventBus: EventBus,
    private val eventStore: EventStore
) {

    suspend fun process(event: SystemEvent) {

        eventStore.save(event)

        eventBus.publish(event)
    }

    suspend fun history(): List<SystemEvent> {

        return eventStore.getEvents()
    }

    suspend fun clear() {

        eventStore.clear()
    }
}
