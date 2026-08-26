package pro.liliya.core.runtime.monitor

import pro.liliya.core.CoreDiagnosticSnapshot
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleRecord

data class RuntimeMonitorSnapshot(
    val diagnostics: CoreDiagnosticSnapshot,
    val healthy: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val lastLifecycleEvent: RuntimeLifecycleRecord? = null,
    val lifecycleHistory: List<RuntimeLifecycleRecord> = emptyList()
)
