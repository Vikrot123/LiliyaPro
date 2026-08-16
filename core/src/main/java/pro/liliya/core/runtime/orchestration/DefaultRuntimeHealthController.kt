package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot

class DefaultRuntimeHealthController(
    private val composition: RuntimeHealthComposition
) : RuntimeHealthController {

    override fun createSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot {
        return composition.createHealthSnapshot(
            state = state,
            telemetry = telemetry,
            failureReason = failureReason
        )
    }

    override fun createReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: pro.liliya.core.runtime.health.RuntimeRecoverySnapshot
    ): RuntimeHealthReport {
        return composition.createHealthReport(
            state = state,
            telemetry = telemetry,
            failure = failure,
            recovery = recovery
        )
    }
}
