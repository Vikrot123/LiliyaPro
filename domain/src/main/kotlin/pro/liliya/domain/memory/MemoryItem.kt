package pro.liliya.domain.memory

data class MemoryItem(
    val id: String,
    val content: String,
    val timestamp: Long,
    val importance: Float = 0.5f
)
