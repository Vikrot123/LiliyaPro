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
        listeners.clear()
    }
}
