package pro.liliya.core

object RuntimeEventBus {

    private val listeners =
        linkedSetOf<(RuntimeEvent) -> Unit>()

    fun subscribe(
        listener: (RuntimeEvent) -> Unit
    ) {
        listeners.add(listener)
    }

    fun unsubscribe(
        listener: (RuntimeEvent) -> Unit
    ) {
        listeners.remove(listener)
    }

    fun publish(
        event: RuntimeEvent
    ) {
        listeners.toList()
            .forEach { listener ->
                try {
                    listener(event)
                } catch (_: Exception) {
                }
            }
    }

    fun clear() {
        listeners.clear()
    }
}
