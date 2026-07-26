package pro.liliya.runtime

import pro.liliya.domain.models.RuntimeState

object RuntimeStatusMapper {

    fun map(state: RuntimeState): RuntimeStatus {
        return when (state) {
            RuntimeState.BOOTING ->
                RuntimeStatus.STARTING

            RuntimeState.INITIALIZING ->
                RuntimeStatus.STARTING

            RuntimeState.READY ->
                RuntimeStatus.READY

            RuntimeState.LISTENING ->
                RuntimeStatus.LISTENING

            RuntimeState.THINKING ->
                RuntimeStatus.PROCESSING

            RuntimeState.RESPONDING ->
                RuntimeStatus.PROCESSING

            RuntimeState.PAUSED ->
                RuntimeStatus.PAUSED

            RuntimeState.ERROR ->
                RuntimeStatus.ERROR

            RuntimeState.STOPPED ->
                RuntimeStatus.STOPPED
        }
    }
}
