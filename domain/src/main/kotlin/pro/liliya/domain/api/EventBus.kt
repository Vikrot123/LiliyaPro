package pro.liliya.domain.api

import kotlinx.coroutines.flow.Flow
import pro.liliya.domain.models.SystemEvent

interface EventBus {

    suspend fun publish(event: SystemEvent)

    fun events(): Flow<SystemEvent>
}
