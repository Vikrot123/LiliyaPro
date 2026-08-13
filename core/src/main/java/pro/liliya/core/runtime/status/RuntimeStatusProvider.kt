package pro.liliya.core.runtime.status

import pro.liliya.core.runtime.health.RuntimeHealthReport

class RuntimeStatusProvider {

    fun createStatus(
        report: RuntimeHealthReport
    ): RuntimeStatusSnapshot {

        return RuntimeStatusSnapshot(
            report = report,
            available = true
        )
    }
}
