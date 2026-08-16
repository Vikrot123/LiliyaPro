package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot

class DefaultRuntimeStatusController(
    private val composition: RuntimeStatusComposition
) : RuntimeStatusController {

    override fun provider(): RuntimeStatusProvider {
        return composition.statusProvider()
    }

    override fun createSnapshot(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot {
        return composition.createRuntimeStatus(
            report = report
        )
    }
}
