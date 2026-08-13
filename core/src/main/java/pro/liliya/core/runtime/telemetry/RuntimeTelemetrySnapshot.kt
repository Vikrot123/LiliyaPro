package pro.liliya.core.runtime.telemetry

import pro.liliya.core.RuntimeEvent

data class RuntimeTelemetrySnapshot(
    val startedAt: Long?,
    val readyAt: Long?,
    val stoppedAt: Long?,
    val eventCount: Int,
    val lastEvent: RuntimeEvent?
)
