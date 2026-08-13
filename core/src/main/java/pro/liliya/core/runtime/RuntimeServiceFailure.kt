package pro.liliya.core.runtime

data class RuntimeServiceFailure(
    val serviceName: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)
