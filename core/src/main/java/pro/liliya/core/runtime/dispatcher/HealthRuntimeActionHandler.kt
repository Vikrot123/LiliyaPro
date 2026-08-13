package pro.liliya.core.runtime.dispatcher

import pro.liliya.core.runtime.action.RuntimeActionExecutor
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.control.RuntimeCommand

class HealthRuntimeActionHandler(
    private val executor: RuntimeActionExecutor
) : RuntimeActionHandler {

    override fun supports(
        request: RuntimeActionRequest
    ): Boolean {
        return request.command == RuntimeCommand.HEALTH_CHECK
    }

    override fun handle(
        request: RuntimeActionRequest
    ): RuntimeActionResult {
        return executor.execute(request)
    }
}
