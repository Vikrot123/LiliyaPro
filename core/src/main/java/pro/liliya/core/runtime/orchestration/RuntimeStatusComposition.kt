package pro.liliya.core.runtime.orchestration

import pro.liliya.core.runtime.health.RuntimeHealthReport
import pro.liliya.core.runtime.status.RuntimeStatusSnapshot
import pro.liliya.core.runtime.status.RuntimeStatusProvider

interface RuntimeStatusComposition {

    fun statusProvider(): RuntimeStatusProvider

    fun createRuntimeStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot
}
