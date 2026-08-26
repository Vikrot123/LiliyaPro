package pro.liliya.core

private fun runtimeEventBusLogger() =
    pro.liliya.core.logging.LoggerFactory.create(
        module = "CORE",
        component = "RuntimeEventBus",
        method = "publish"
    )

object RuntimeEventBus {

    private val listeners =
        linkedSetOf<(RuntimeEvent) -> Unit>()

    fun subscribe(
        listener: (RuntimeEvent) -> Unit
    ) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun unsubscribe(
        listener: (RuntimeEvent) -> Unit
    ) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    fun publish(
        event: RuntimeEvent
    ) {
        val snapshot =
            synchronized(listeners) {
                listeners.toList()
            }

        snapshot.forEach { listener ->
            try {
                listener(event)
            } catch (e: Exception) {
                runtimeEventBusLogger().error(
                    pro.liliya.core.logging.LoggerMarkers.ERROR,
                    "Runtime event listener failed",
                    e
                )
            }
        }
    }

    fun clear() {
        synchronized(listeners) {
            listeners.clear()
        }
    }
}
