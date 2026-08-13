package pro.liliya.core.runtime.health

data class RuntimeHealthReport(
    val runtime: RuntimeHealthSnapshot,
    val failure: RuntimeFailureHealthSnapshot,
    val recovery: RuntimeRecoverySnapshot
)
