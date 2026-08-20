package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeObserverTelemetryBridgeRestartIsolationContractTest {

    @Test
    fun runtime_observer_and_telemetry_bridges_restart_without_duplicate_delivery() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                events.add(event)
            }
        }

        composition.observerRegistry()
            .subscribe(observer)

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val beforeRestart = composition.telemetryObserver()
            .snapshot()

        composition.stopRuntimeBridges()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val afterRestart = composition.telemetryObserver()
            .snapshot()

        assertEquals(
            2,
            events.count { it == RuntimeEvent.RuntimeReady }
        )

        assertEquals(
            2,
            afterRestart.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            afterRestart.lastEvent
        )

        assertEquals(
            beforeRestart.startedAt,
            afterRestart.startedAt
        )

        composition.stopRuntimeBridges()

        composition.observerRegistry()
            .unsubscribe(observer)

        RuntimeEventBus.clear()
    }
}
