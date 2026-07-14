package pro.liliya.core.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pro.liliya.domain.api.EventBus
import pro.liliya.domain.models.SystemEvent

class EventBusImpl : EventBus {

    private val _events = MutableSharedFlow<SystemEvent>(
        extraBufferCapacity = 64
    )

    override suspend fun publish(event: SystemEvent) {
        _events.emit(event)
    }

    override fun events(): Flow<SystemEvent> {
        return _events.asSharedFlow()
    }
}
