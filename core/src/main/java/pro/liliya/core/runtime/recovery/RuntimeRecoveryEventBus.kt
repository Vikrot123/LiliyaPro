package pro.liliya.core.runtime.recovery

object RuntimeRecoveryEventBus {

    private val listeners =
        mutableListOf<(RuntimeRecoveryEvent) -> Unit>()

    fun subscribe(
        listener: (RuntimeRecoveryEvent) -> Unit
    ) {
        listeners += listener
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
