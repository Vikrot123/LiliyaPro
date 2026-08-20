package pro.liliya.core.runtime.observer

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeObserverBridge(
    private val registry: DefaultRuntimeObserverRegistry
) {
    private val listener: (RuntimeEvent) -> Unit = { event ->
        registry.publish(event)
    }

    fun install() {
        RuntimeEventBus.subscribe(listener)
    }

    fun uninstall() {
        RuntimeEventBus.unsubscribe(listener)
    }
}
