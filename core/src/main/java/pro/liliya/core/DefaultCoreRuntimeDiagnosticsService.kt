package pro.liliya.core

class DefaultCoreRuntimeDiagnosticsService(
    private val source: CoreDiagnosticSource
) : CoreRuntimeDiagnosticsService {

    override fun snapshot(): CoreRuntimeDiagnosticsSnapshot {
        val snapshot = source.snapshot()

        return CoreRuntimeDiagnosticsSnapshot(
            runtimeState = snapshot.runtimeState,
            moduleStates = snapshot.moduleStates,
            serviceStates = snapshot.runtimeServiceStates,
            serviceFailures = snapshot.runtimeServiceFailures,
            serviceHealth = snapshot.runtimeServiceHealth,
            recoverySnapshot = snapshot.runtimeRecoverySnapshot,
            failureReason = snapshot.failureReason
        )
    }
}
