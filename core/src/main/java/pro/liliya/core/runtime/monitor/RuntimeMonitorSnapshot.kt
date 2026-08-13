package pro.liliya.core.runtime.monitor

import pro.liliya.core.CoreRuntimeDiagnosticsSnapshot

data class RuntimeMonitorSnapshot(
    val diagnostics: CoreRuntimeDiagnosticsSnapshot,
    val healthy: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
