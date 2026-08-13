package pro.liliya.core.runtime.health

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.telemetry.RuntimeTelemetrySnapshot

class RuntimeHealthProvider {

    fun createSnapshot(
        state: CoreRuntimeState,
        telemetry: RuntimeTelemetrySnapshot,
        failureReason: String?
    ): RuntimeHealthSnapshot {

        val uptime =
            if (telemetry.startedAt != null) {
                val end =
                    telemetry.readyAt
                        ?: System.currentTimeMillis()

                end - telemetry.startedAt
            } else {
                null
            }

        return RuntimeHealthSnapshot(
            state = state,
            uptimeMillis = uptime,
            eventCount = telemetry.eventCount,
            lastEvent = telemetry.lastEvent,
            startedAt = telemetry.startedAt,
            readyAt = telemetry.readyAt,
            stoppedAt = telemetry.stoppedAt,
            failureReason = failureReason
        )
    }
}
