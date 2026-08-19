package pro.liliya.core.runtime.recovery

sealed class RuntimeRecoveryEvent {

    data class Started(
        val serviceName: String
    ) : RuntimeRecoveryEvent()

    data class Completed(
        val serviceName: String
    ) : RuntimeRecoveryEvent()

    data class Failed(
        val serviceName: String
    ) : RuntimeRecoveryEvent()
}
