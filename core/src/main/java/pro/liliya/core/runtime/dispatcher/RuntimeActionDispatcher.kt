package pro.liliya.core.runtime.dispatcher

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult

class RuntimeActionDispatcher(
    private val registry: RuntimeActionHandlerRegistry
) {

    fun dispatch(
        request: RuntimeActionRequest
    ): RuntimeActionResult {

        val handler =
            registry.find {
                it.supports(request)
            }

        return handler?.handle(request)
            ?: throw IllegalStateException(
                "No action handler for ${request.command}"
            )
    }
}
