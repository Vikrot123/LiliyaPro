package pro.liliya.core.runtime.telemetry.composition

import pro.liliya.core.runtime.health.RuntimeHealthProvider
import pro.liliya.core.runtime.health.RuntimeHealthReportProvider
import pro.liliya.core.runtime.status.RuntimeStatusProvider
import pro.liliya.core.runtime.telemetry.RuntimeTelemetryObserver

interface RuntimeTelemetryProviderComposition {

    fun telemetryObserver(): RuntimeTelemetryObserver

    fun healthProvider(): RuntimeHealthProvider

    fun healthReportProvider(): RuntimeHealthReportProvider

    fun statusProvider(): RuntimeStatusProvider
}
