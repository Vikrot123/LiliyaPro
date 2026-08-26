package pro.liliya.core

private fun moduleEventBusLogger() =
    pro.liliya.core.logging.LoggerFactory.create(
        module = "CORE",
        component = "ModuleEventBus",
        method = "publish"
    )

object ModuleEventBus {

    private val listeners =
        linkedSetOf<(ModuleEvent) -> Unit>()

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
            } catch (e: Exception) {
                moduleEventBusLogger().error(
                    pro.liliya.core.logging.LoggerMarkers.ERROR,
                    "Module event listener failed",
                    e
                )
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
