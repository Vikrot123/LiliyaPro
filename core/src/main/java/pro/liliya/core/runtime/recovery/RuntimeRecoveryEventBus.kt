package pro.liliya.core.runtime.recovery

class RuntimeRecoveryEventBus {

    private val listeners =
        linkedSetOf<(RuntimeRecoveryEvent) -> Unit>()

    fun subscribe(
        listener: (RuntimeRecoveryEvent) -> Unit
    ) {
        listeners.add(listener)
    }

    fun unsubscribe(
        listener: (RuntimeRecoveryEvent) -> Unit
    ) {
        listeners -= listener
    }

    fun publish(
        event: RuntimeRecoveryEvent
    ) {
        listeners.toList()
            .forEach { listener ->
                listener(event)
            }
    }

    fun clear() {
        listeners.clear()
    }
}
