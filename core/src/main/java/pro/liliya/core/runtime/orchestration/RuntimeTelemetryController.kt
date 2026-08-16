package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver

interface RuntimeTelemetryController {

    fun observer(): RuntimeTelemetryObserver

    fun createStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot
}
