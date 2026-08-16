package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.health.RuntimeHealthSnapshot
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot
import pro.liliya.core.runtime.health.RuntimeFailureHealthSnapshot
import pro.liliya.core.runtime.health.RuntimeRecoverySnapshot

interface RuntimeHealthComposition {

    fun healthProvider(): RuntimeHealthProvider

    fun createHealthSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot

    fun createHealthReport(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failure: RuntimeFailureHealthSnapshot,
        recovery: RuntimeRecoverySnapshot
    ): RuntimeHealthReport
}
