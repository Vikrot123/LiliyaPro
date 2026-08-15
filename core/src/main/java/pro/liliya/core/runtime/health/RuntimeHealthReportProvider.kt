package pro.liliya.core.runtime.health

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot

class RuntimeHealthReportProvider(
    private val healthProvider: RuntimeHealthProvider
) {

    fun createReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: RuntimeRecoverySnapshot
    ): RuntimeHealthReport {

        return RuntimeHealthReport(
            runtime = healthProvider.createSnapshot(
                state = state,
                telemetry = telemetry,
                failureReason = failure.failureReason
            ),
            failure = failure,
            recovery = recovery
        )
    }
}
