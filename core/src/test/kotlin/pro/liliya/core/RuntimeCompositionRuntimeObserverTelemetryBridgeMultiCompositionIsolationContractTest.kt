package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeObserverTelemetryBridgeMultiCompositionIsolationContractTest {

    @Test
    fun runtime_observer_and_telemetry_bridges_multi_composition_isolation_is_preserved() {
        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        val firstEvents = mutableListOf<RuntimeEvent>()
        val secondEvents = mutableListOf<RuntimeEvent>()

        val firstObserver = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                firstEvents.add(event)
            }
        }

        val secondObserver = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                secondEvents.add(event)
            }
        }

        first.observerRegistry()
            .subscribe(firstObserver)

        second.observerRegistry()
            .subscribe(secondObserver)

        first.installRuntimeObserverBridge()
        second.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstBeforeStop = first.telemetryObserver()
            .snapshot()

        val secondBeforeStop = second.telemetryObserver()
            .snapshot()

        first.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstAfterStop = first.telemetryObserver()
            .snapshot()

        val secondAfterStop = second.telemetryObserver()
            .snapshot()

        assertEquals(
            1,
            firstEvents.count { it == RuntimeEvent.RuntimeReady }
        )

        assertEquals(
            2,
            secondEvents.count { it == RuntimeEvent.RuntimeReady }
        )

        assertEquals(
            firstBeforeStop,
            firstAfterStop
        )

        assertEquals(
            2,
            secondAfterStop.eventCount
        )

        first.observerRegistry()
            .unsubscribe(firstObserver)

        second.observerRegistry()
            .unsubscribe(secondObserver)

        second.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
