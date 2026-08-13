package pro.liliya.core.runtime.dispatcher

import pro.liliya.core.CoreRuntime
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.control.RuntimeControlResult

class RuntimeActionDispatcher(
    private val registry: RuntimeActionHandlerRegistry
) {

    fun dispatch(
        request: RuntimeActionRequest
    ): RuntimeActionResult {

        val handler = registry.find {
            it.supports(request)
        }

        return handler?.handle(request)
            ?: RuntimeActionResult(
                request = request,
                success = false,
                controlResult = RuntimeControlResult(
                    command = request.command,
                    success = false,
                    previousState = CoreRuntime.getRuntimeState(),
                    currentState = CoreRuntime.getRuntimeState(),
                    status = CoreRuntime.getRuntimeStatusSnapshot(),
                    message = "No action handler for ${request.command}"
                )
            )
    }
}
