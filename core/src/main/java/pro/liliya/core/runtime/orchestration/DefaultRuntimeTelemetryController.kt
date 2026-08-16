package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver

class DefaultRuntimeTelemetryController(
    private val composition: RuntimeTelemetryComposition
) : RuntimeTelemetryController {

    override fun observer(): RuntimeTelemetryObserver {
        return composition.telemetryObserver()
    }

    override fun createStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot {
        return composition.createRuntimeStatus(
            report = report
        )
    }
}
