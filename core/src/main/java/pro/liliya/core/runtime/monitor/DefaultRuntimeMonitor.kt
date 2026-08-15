package pro.liliya.core.runtime.monitor

import pro.liliya.core.runtime.composition.RuntimeComposition

class DefaultRuntimeMonitor(
    private val runtimeComposition: RuntimeComposition
) : RuntimeMonitor {

    override fun snapshot(): RuntimeMonitorSnapshot {
        val diagnostics =
            runtimeComposition.diagnosticsService().snapshot()

        val lifecycleRecorder =
            runtimeComposition.lifecycleRecorder()

        val lifecycleHistory =
            lifecycleRecorder.records()

        return RuntimeMonitorSnapshot(
            diagnostics = diagnostics,
            healthy = diagnostics.serviceHealth.values.all { it.healthy },
            lastLifecycleEvent = lifecycleRecorder.last(),
            lifecycleHistory = lifecycleHistory
        )
    }
}
