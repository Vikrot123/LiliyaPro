package pro.liliya.core.runtime.health

class RuntimeRecoveryTracker {

    private var recoveredAt: Long? = null

    fun markRecovered() {
        recoveredAt = System.currentTimeMillis()
    }

    fun clear() {
        recoveredAt = null
    }

    fun snapshot(): RuntimeRecoverySnapshot {
        return RuntimeRecoverySnapshot(
            recovered = recoveredAt != null,
            recoveredAt = recoveredAt
        )
    }
}
