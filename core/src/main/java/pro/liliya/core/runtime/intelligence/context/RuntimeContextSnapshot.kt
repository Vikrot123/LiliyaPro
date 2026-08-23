package pro.liliya.core.runtime.intelligence.context

data class RuntimeContextSnapshot(
    val runtimeState: String,
    val activeServices: List<String>,
    val timestamp: Long
)
