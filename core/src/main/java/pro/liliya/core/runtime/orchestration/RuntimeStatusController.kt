package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot

interface RuntimeStatusController {

    fun provider(): RuntimeStatusProvider

    fun createSnapshot(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot
}
