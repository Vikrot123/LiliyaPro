package pro.liliya.core.runtime.observer

import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeObserverBridge(
    private val registry: DefaultRuntimeObserverRegistry
) {

    fun install() {
        RuntimeEventBus.subscribe { event ->
            registry.publish(event)
        }
    }
}
