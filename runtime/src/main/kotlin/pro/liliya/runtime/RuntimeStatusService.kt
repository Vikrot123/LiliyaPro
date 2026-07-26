package pro.liliya.runtime

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RuntimeStatusService {

    private val _status =
        MutableStateFlow(RuntimeStatus.STOPPED)

    val status: StateFlow<RuntimeStatus> =
        _status.asStateFlow()

    fun update(status: RuntimeStatus) {
        _status.value = status
    }
}
