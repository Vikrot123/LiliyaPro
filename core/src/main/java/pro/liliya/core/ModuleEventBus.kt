package pro.liliya.core

object ModuleEventBus {

    private val listeners =
        mutableListOf<(ModuleEvent) -> Unit>()

    fun subscribe(
        listener: (ModuleEvent) -> Unit
    ) {
        listeners.add(listener)
    }

    fun unsubscribe(
        listener: (ModuleEvent) -> Unit
    ) {
        listeners.remove(listener)
    }

    fun publish(
        event: ModuleEvent
    ) {
        listeners.toList().forEach { listener ->
            try {
                listener(event)
            } catch (_: Exception) {
            }
        }
    }

    fun clear() {
        listeners.clear()
    }

    fun hasListeners(): Boolean {
        return listeners.isNotEmpty()
    }
}
