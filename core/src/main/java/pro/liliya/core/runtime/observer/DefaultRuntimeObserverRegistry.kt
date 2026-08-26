package pro.liliya.core.runtime.observer

import pro.liliya.core.RuntimeEvent

class DefaultRuntimeObserverRegistry :
    RuntimeObserverRegistry {

    private val observers =
        mutableListOf<RuntimeObserver>()

    override fun subscribe(
        observer: RuntimeObserver
    ) {
        synchronized(observers) {
            if (!observers.contains(observer)) {
                observers.add(observer)
            }
        }
    }

    override fun unsubscribe(
        observer: RuntimeObserver
    ) {
        synchronized(observers) {
            observers.remove(observer)
        }
    }

    fun publish(
        event: RuntimeEvent
    ) {
        val snapshot =
            synchronized(observers) {
                observers.toList()
            }

        snapshot.forEach { observer ->
            observer.onRuntimeEvent(event)
        }
    }
}
