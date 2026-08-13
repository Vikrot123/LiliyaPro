package pro.liliya.core.runtime.health

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.RuntimeEvent

data class RuntimeHealthSnapshot(
    val state: CoreRuntimeState,
    val uptimeMillis: Long?,
    val eventCount: Int,
    val lastEvent: RuntimeEvent?,
    val startedAt: Long?,
    val readyAt: Long?,
    val stoppedAt: Long?,
    val failureReason: String?
)
