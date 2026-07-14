package pro.liliya.core.event

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pro.liliya.domain.api.EventStore
import pro.liliya.domain.models.SystemEvent

class InMemoryEventStoreImpl : EventStore {

    private val mutex = Mutex()

    private val events = mutableListOf<SystemEvent>()

    override suspend fun save(event: SystemEvent) {
        mutex.withLock {
            events.add(event)
        }
    }

    override suspend fun getEvents(): List<SystemEvent> {
        return mutex.withLock {
            events.toList()
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            events.clear()
        }
    }
}
