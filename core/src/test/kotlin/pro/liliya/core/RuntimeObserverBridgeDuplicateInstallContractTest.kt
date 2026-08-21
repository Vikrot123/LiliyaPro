package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.observer.DefaultRuntimeObserverRegistry
import pro.liliya.core.runtime.observer.RuntimeObserver
import pro.liliya.core.runtime.observer.RuntimeObserverBridge

class RuntimeObserverBridgeDuplicateInstallContractTest {

    @Test
    fun observer_bridge_duplicate_install_must_not_duplicate_delivery() {
        RuntimeEventBus.clear()

        val registry = DefaultRuntimeObserverRegistry()

        val events = mutableListOf<RuntimeEvent>()

        registry.subscribe(
            object : RuntimeObserver {
                override fun onRuntimeEvent(event: RuntimeEvent) {
                    events.add(event)
                }
            }
        )

        val bridge = RuntimeObserverBridge(registry)

        bridge.install()
        bridge.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            1,
            events.size
        )

        bridge.uninstall()
        RuntimeEventBus.clear()
    }
}
