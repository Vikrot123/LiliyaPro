package pro.liliya.core.runtime

data class RuntimeRecoverySnapshot(
    val restartCounts: Map<String, Int>,
    val lastRecoveredService: String?,
    val lastRecoverySuccessful: Boolean?
)
