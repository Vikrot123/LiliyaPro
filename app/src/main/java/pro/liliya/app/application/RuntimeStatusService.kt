package pro.liliya.app.application

import kotlinx.coroutines.flow.StateFlow
import pro.liliya.runtime.RuntimeStatus
import pro.liliya.runtime.RuntimeStatusService as RuntimeService

class RuntimeStatusService {

    fun status(): StateFlow<RuntimeStatus> {
        return RuntimeService.status
    }

    fun currentState(): RuntimeStatus {
        return RuntimeService.status.value
    }
}
