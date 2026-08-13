package pro.liliya.core

fun CoreDiagnosticSnapshot.toRuntimeDiagnosticsSnapshot(): CoreRuntimeDiagnosticsSnapshot {
    return CoreRuntimeDiagnosticsSnapshot(
        runtimeState = runtimeState,
        moduleStates = moduleStates,
        serviceStates = runtimeServiceStates,
        serviceFailures = runtimeServiceFailures,
        serviceHealth = runtimeServiceHealth,
        recoverySnapshot = runtimeRecoverySnapshot,
        failureReason = failureReason
    )
}
