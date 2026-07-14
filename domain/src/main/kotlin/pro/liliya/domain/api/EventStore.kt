package pro.liliya.domain.api

import pro.liliya.domain.models.SystemEvent

interface EventStore {

    suspend fun save(event: SystemEvent)

    suspend fun getEvents(): List<SystemEvent>

    suspend fun clear()
}
