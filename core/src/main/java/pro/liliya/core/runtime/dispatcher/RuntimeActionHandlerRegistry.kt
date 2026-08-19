package pro.liliya.core.runtime.dispatcher

class RuntimeActionHandlerRegistry {

    private val handlers =
        mutableListOf<RuntimeActionHandler>()

    fun register(
        handler: RuntimeActionHandler
    ) {
        if (!handlers.contains(handler)) {
            handlers.add(handler)
        }
    }

    fun find(
        predicate: (RuntimeActionHandler) -> Boolean
    ): RuntimeActionHandler? {
        return handlers.firstOrNull(predicate)
    }

    fun snapshot(): List<RuntimeActionHandler> {
        return handlers.toList()
    }

    fun clear() {
        handlers.clear()
    }
}
