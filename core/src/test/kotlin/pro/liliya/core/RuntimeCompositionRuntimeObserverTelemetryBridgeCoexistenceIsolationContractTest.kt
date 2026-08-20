package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeObserverTelemetryBridgeCoexistenceIsolationContractTest {

    @Test
    fun runtime_observer_and_telemetry_bridges_coexist_without_duplicate_delivery() {
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

        val snapshot = composition.telemetryObserver()
            .snapshot()

        assertEquals(
            1,
            events.count { it == RuntimeEvent.RuntimeReady }
        )

        assertEquals(
            1,
            snapshot.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            snapshot.lastEvent
        )

        composition.stopRuntimeBridges()

        composition.observerRegistry()
            .unsubscribe(observer)

        RuntimeEventBus.clear()
    }
}
