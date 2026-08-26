package pro.liliya.core.runtime.dispatcher

class RuntimeActionHandlerRegistry {

    private val handlers =
        mutableListOf<RuntimeActionHandler>()

    fun register(
        handler: RuntimeActionHandler
    ) {
        synchronized(handlers) {
            if (!handlers.contains(handler)) {
                handlers.add(handler)
            }
        }
    }

    fun find(
        predicate: (RuntimeActionHandler) -> Boolean
    ): RuntimeActionHandler? {

        val snapshot =
            synchronized(handlers) {
                handlers.toList()
            }

        return snapshot.firstOrNull(
            predicate
        )
    }

    fun snapshot(): List<RuntimeActionHandler> {
        return synchronized(handlers) {
            handlers.toList()
        }
    }

    fun clear() {
        synchronized(handlers) {
            handlers.clear()
        }
    }
}
