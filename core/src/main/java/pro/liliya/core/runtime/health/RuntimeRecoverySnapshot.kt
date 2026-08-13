package pro.liliya.core.runtime.health

data class RuntimeRecoverySnapshot(
    val recovered: Boolean,
    val recoveredAt: Long?
)
