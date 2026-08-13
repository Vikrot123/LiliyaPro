package pro.liliya.core.runtime.monitor

import pro.liliya.core.CoreRuntimeDiagnosticsService

class DefaultRuntimeMonitor(
    private val diagnosticsService: CoreRuntimeDiagnosticsService
) : RuntimeMonitor {

    override fun snapshot(): RuntimeMonitorSnapshot {
        val diagnostics = diagnosticsService.snapshot()

        return RuntimeMonitorSnapshot(
            diagnostics = diagnostics,
            healthy = diagnostics.serviceHealth.values.all { it.healthy }
        )
    }
}
