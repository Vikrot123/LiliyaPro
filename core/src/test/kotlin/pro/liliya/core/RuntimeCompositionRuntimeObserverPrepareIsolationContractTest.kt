package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeObserverPrepareIsolationContractTest {

    @Test
    fun runtime_observer_bridge_does_not_duplicate_after_prepare_runtime() {
        RuntimeEventBus.clear()
        
        val events = mutableListOf<RuntimeEvent>()

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                events.add(event)
            }
        }

        val composition = DefaultRuntimeComposition()

        composition.observerRegistry()
            .subscribe(observer)

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        composition.prepareRuntime()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            2,
            events.count { it is RuntimeEvent.RuntimeReady }
        )

        composition.stopRuntimeBridges()

        composition.observerRegistry()
            .unsubscribe(observer)

        RuntimeEventBus.clear()
    }
}
