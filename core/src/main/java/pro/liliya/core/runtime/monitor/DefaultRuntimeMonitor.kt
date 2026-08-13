package pro.liliya.core.runtime.monitor

import pro.liliya.core.CoreRuntimeDiagnosticsService
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecorder

class DefaultRuntimeMonitor(
    private val diagnosticsService: CoreRuntimeDiagnosticsService,
    private val lifecycleRecorder: RuntimeLifecycleRecorder
) : RuntimeMonitor {

    override fun snapshot(): RuntimeMonitorSnapshot {
        val diagnostics = diagnosticsService.snapshot()
        val lifecycleHistory = lifecycleRecorder.records()

        return RuntimeMonitorSnapshot(
            diagnostics = diagnostics,
            healthy = diagnostics.serviceHealth.values.all { it.healthy },
            lastLifecycleEvent = lifecycleRecorder.last(),
            lifecycleHistory = lifecycleHistory
        )
    }
}
