package pro.liliya.core.runtime

data class RuntimeServiceHealth(
    val name: String,
    val state: RuntimeServiceState,
    val healthy: Boolean,
    val lastFailure: RuntimeServiceFailure? = null
)
