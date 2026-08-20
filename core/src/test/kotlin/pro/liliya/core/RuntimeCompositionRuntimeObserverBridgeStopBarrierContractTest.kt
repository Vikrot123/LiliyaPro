package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeObserverBridgeStopBarrierContractTest {

    @Test
    fun stop_runtime_bridges_blocks_runtime_observer_delivery() {

        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()
        val received = mutableListOf<RuntimeEvent>()

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                received.add(event)
            }
        }

        composition.observerRegistry()
            .subscribe(observer)

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        composition.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            1,
            received.count { it == RuntimeEvent.RuntimeReady }
        )

        composition.observerRegistry()
            .unsubscribe(observer)

        RuntimeEventBus.clear()
    }
}
