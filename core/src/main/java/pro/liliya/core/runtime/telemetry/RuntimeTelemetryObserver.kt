package pro.liliya.core.runtime.telemetry

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeTelemetryObserver : RuntimeObserver {

    private var startedAt: Long? = null
    private var readyAt: Long? = null
    private var stoppedAt: Long? = null

    private var eventCount = 0
    private var lastEvent: RuntimeEvent? = null


    override fun onRuntimeEvent(event: RuntimeEvent) {
        println("TELEMETRY_RECEIVED=$event")

        eventCount++
        lastEvent = event

        when (event) {

            RuntimeEvent.SystemStart -> {
                startedAt = System.currentTimeMillis()
            }

            RuntimeEvent.RuntimeReady -> {
                readyAt = System.currentTimeMillis()
            }

            RuntimeEvent.SystemStop -> {
                stoppedAt = System.currentTimeMillis()
            }

            else -> {
            }
        }
    }


    fun reset() {
        startedAt = null
        readyAt = null
        stoppedAt = null
        eventCount = 0
        lastEvent = null
    }

    fun snapshot(): RuntimeTelemetrySnapshot {
        return RuntimeTelemetrySnapshot(
            startedAt = startedAt,
            readyAt = readyAt,
            stoppedAt = stoppedAt,
            eventCount = eventCount,
            lastEvent = lastEvent
        )
    }
}
