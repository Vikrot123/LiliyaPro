package pro.liliya.core

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeObserverBridgeLifecycleContractTest {

    @Test
    fun runtimeObserverBridgeCanRestartWithoutDuplicateDelivery() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()
        val received = mutableListOf<RuntimeEvent>()

        composition.observerRegistry().subscribe(
            object : RuntimeObserver {
                override fun onRuntimeEvent(event: RuntimeEvent) {
                    received.add(event)
                }
            }
        )

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        composition.uninstallRuntimeObserverBridge()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            2,
            received.count { it == RuntimeEvent.RuntimeReady }
        )

        composition.uninstallRuntimeObserverBridge()

        RuntimeEventBus.clear()
    }
}
