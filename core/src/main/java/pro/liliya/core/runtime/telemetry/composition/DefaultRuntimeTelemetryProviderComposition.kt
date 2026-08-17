package pro.liliya.core.runtime.telemetry.composition

import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver

class DefaultRuntimeTelemetryProviderComposition(

    private val observer: RuntimeTelemetryObserver,
    private val health: RuntimeHealthProvider,
    private val report: RuntimeHealthReportProvider,
    private val status: RuntimeStatusProvider

) : RuntimeTelemetryProviderComposition {

    override fun telemetryObserver(): RuntimeTelemetryObserver {
        return observer
    }

    override fun healthProvider(): RuntimeHealthProvider {
        return health
    }

    override fun healthReportProvider(): RuntimeHealthReportProvider {
        return report
    }

    override fun statusProvider(): RuntimeStatusProvider {
        return status
    }
}
