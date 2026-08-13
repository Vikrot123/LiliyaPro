package pro.liliya.core.runtime.dispatcher

class RuntimeActionHandlerRegistry {

    private val handlers =
        mutableListOf<RuntimeActionHandler>()

    fun register(
        handler: RuntimeActionHandler
    ) {
        handlers.add(handler)
    }

    fun find(
        predicate: (RuntimeActionHandler) -> Boolean
    ): RuntimeActionHandler? {
        return handlers.firstOrNull(predicate)
    }

    fun clear() {
        handlers.clear()
    }
}
