package pro.liliya.domain.models

data class StateTransition(

    val from: RuntimeState,

    val to: RuntimeState,

    val timestamp: Long = System.currentTimeMillis()
)
