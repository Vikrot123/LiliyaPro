package pro.liliya.core.runtime.observer

import pro.liliya.core.RuntimeEvent

class DefaultRuntimeObserverRegistry :
    RuntimeObserverRegistry {

    private val observers =
        mutableListOf<RuntimeObserver>()

    override fun subscribe(observer: RuntimeObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    override fun unsubscribe(observer: RuntimeObserver) {
        observers.remove(observer)
    }

    fun publish(event: RuntimeEvent) {
        observers.toList()
            .forEach { observer ->
                observer.onRuntimeEvent(event)
            }
    }
}
