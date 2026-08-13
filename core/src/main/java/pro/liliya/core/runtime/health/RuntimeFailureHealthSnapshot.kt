package pro.liliya.core.runtime.health

data class RuntimeFailureHealthSnapshot(
    val failed: Boolean,
    val failureReason: String?,
    val failureTimestamp: Long?,
    val failedModule: String?
)
