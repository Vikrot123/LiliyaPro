package pro.liliya.core.runtime.observer

interface RuntimeObserverRegistry {

    fun subscribe(observer: RuntimeObserver)

    fun unsubscribe(observer: RuntimeObserver)
}
