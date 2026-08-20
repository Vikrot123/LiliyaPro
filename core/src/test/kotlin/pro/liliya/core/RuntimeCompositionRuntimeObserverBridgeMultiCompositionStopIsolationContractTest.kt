package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeObserverBridgeMultiCompositionStopIsolationContractTest {

    @Test
    fun stopped_composition_does_not_block_other_runtime_observer_bridge_delivery() {

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

        first.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            1,
            firstEvents.count { it == RuntimeEvent.RuntimeReady }
        )

        assertEquals(
            2,
            secondEvents.count { it == RuntimeEvent.RuntimeReady }
        )

        first.observerRegistry()
            .unsubscribe(firstObserver)

        second.observerRegistry()
            .unsubscribe(secondObserver)

        RuntimeEventBus.clear()
    }
}
