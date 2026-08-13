package pro.liliya.core.runtime.health

class RuntimeFailureTracker {

    private var failureReason: String? = null
    private var failureTimestamp: Long? = null
    private var failedModule: String? = null

    fun recordFailure(
        reason: String?,
        module: String?
    ) {
        failureReason = reason
        failureTimestamp = System.currentTimeMillis()
        failedModule = module
    }

    fun clear() {
        failureReason = null
        failureTimestamp = null
        failedModule = null
    }

    fun snapshot(): RuntimeFailureHealthSnapshot {
        return RuntimeFailureHealthSnapshot(
            failed = failureReason != null,
            failureReason = failureReason,
            failureTimestamp = failureTimestamp,
            failedModule = failedModule
        )
    }
}
