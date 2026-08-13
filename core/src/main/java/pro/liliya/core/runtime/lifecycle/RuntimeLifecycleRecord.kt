package pro.liliya.core.runtime.lifecycle

data class RuntimeLifecycleRecord(
    val event: RuntimeLifecycleEvent,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String? = null
)
