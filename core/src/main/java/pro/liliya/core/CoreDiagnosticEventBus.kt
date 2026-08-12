package pro.liliya.core

class CoreDiagnosticEventBus {

    private val listeners =
        mutableListOf<CoreDiagnosticEventListener>()

    fun register(
        listener: CoreDiagnosticEventListener
    ) {
        listeners.add(listener)
    }

    fun unregister(
        listener: CoreDiagnosticEventListener
    ) {
        listeners.remove(listener)
    }

    fun publish(
        event: CoreDiagnosticEvent
    ) {
        listeners.forEach { listener ->
            listener.onDiagnosticEvent(event)
        }
    }
}
