package pro.liliya.domain.models

sealed interface SystemEvent {

    val timestamp: Long

    data class UserMessageReceived(
        val message: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : SystemEvent


    data class CycleCompleted(
        val cycleId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : SystemEvent
}
