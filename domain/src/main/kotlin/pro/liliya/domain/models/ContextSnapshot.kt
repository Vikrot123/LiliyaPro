package pro.liliya.domain.models

data class ContextSnapshot(
    val userInput: String,
    val timestamp: Long,
    val stateId: String
)
