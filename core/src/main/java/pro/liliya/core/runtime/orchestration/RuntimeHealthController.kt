package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot

interface RuntimeHealthController {

    fun createSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot

    fun createReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: pro.liliya.core.runtime.health.RuntimeRecoverySnapshot
    ): RuntimeHealthReport
}
