package pro.liliya.core.runtime.status

import pro.liliya.core.runtime.health.RuntimeHealthReport

data class RuntimeStatusSnapshot(
    val report: RuntimeHealthReport,
    val available: Boolean
)
