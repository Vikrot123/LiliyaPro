package pro.liliya.domain.models

data class Episode(
    val id: String,
    val events: List<SystemEvent>,
    val createdAt: Long = System.currentTimeMillis()
)
