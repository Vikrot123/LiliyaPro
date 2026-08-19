package pro.liliya.core.runtime.dispatcher

import pro.liliya.core.runtime.action.RuntimeActionExecutor
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeRecoverActionHandler(
    private val executor: RuntimeActionExecutor
) : RuntimeActionHandler {

    override fun supports(
        request: RuntimeActionRequest
    ): Boolean {
        return request.command == RuntimeCommand.RECOVER
    }

    override fun handle(
        request: RuntimeActionRequest
    ): RuntimeActionResult {
        return executor.execute(request)
    }
}
