package pro.liliya.core

object RuntimeEventBus {

    private val listeners =
        mutableListOf<(RuntimeEvent) -> Unit>()

    fun subscribe(
        listener: (RuntimeEvent) -> Unit
    ) {
        listeners.add(listener)
    }

    fun publish(
        event: RuntimeEvent
    ) {
        listeners.forEach { listener ->
            listener(event)
        }
    }

    fun clear() {
        listeners.clear()
    }
}
