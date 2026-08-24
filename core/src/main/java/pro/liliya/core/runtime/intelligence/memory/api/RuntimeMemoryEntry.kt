package pro.liliya.core.runtime.intelligence.memory.api

data class RuntimeMemoryEntry(

    val id: String,

    val content: String,

    val type: RuntimeMemoryType,

    val confidence: Double,

    val createdAt: Long
)
