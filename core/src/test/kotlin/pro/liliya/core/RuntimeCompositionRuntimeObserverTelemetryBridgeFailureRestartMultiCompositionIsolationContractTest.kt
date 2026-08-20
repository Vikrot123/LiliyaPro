package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeObserverTelemetryBridgeFailureRestartMultiCompositionIsolationContractTest {

    @Test
    fun runtime_observer_and_telemetry_failure_restart_multi_composition_isolation_is_preserved() {
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

        first.observerRegistry().subscribe(firstObserver)
        second.observerRegistry().subscribe(secondObserver)

        first.installRuntimeObserverBridge()
        second.installRuntimeObserverBridge()

        val failure = RuntimeEvent.ModuleFailed(
            "test-module",
            "failure"
        )

        RuntimeEventBus.publish(failure)

        val firstBeforeRestart = first.telemetryObserver().snapshot()
        val secondBeforeRestart = second.telemetryObserver().snapshot()

        first.stopRuntimeBridges()

        first.installRuntimeObserverBridge()

        RuntimeEventBus.publish(failure)

        val firstAfterRestart = first.telemetryObserver().snapshot()
        val secondAfterRestart = second.telemetryObserver().snapshot()


          

        assertEquals(
            2,

            firstEvents.count { it == failure }
        )

        assertEquals(
            2,
            secondEvents.count { it == failure }
        )

        assertEquals(
            2,
            firstAfterRestart.eventCount
        )

        assertEquals(
            2,
            secondAfterRestart.eventCount
        )

        first.observerRegistry()
            .unsubscribe(firstObserver)

        second.observerRegistry()
            .unsubscribe(secondObserver)

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
