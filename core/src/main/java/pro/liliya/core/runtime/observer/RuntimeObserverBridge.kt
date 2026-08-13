package pro.liliya.core.runtime.observer

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeObserverBridge(
    private val registry: DefaultRuntimeObserverRegistry
) {

    private var installed = false

    private val listener: (RuntimeEvent) -> Unit = { event ->
        registry.publish(event)
    }

    fun install() {
        if (installed) {
            return
        }

        RuntimeEventBus.subscribe(listener)
        installed = true
    }

    fun uninstall() {
        if (!installed) {
            return
        }

        RuntimeEventBus.unsubscribe(listener)
        installed = false
    }
}
