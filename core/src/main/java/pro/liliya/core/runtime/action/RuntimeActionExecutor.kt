package pro.liliya.core.runtime.action

import pro.liliya.core.CoreRuntime

class RuntimeActionExecutor {

    fun execute(
        request: RuntimeActionRequest
    ): RuntimeActionResult {

        val result = CoreRuntime.executeRuntimeCommand(
            request.command
        )

        return RuntimeActionResult(
            request = request,
            success = result.success,
            controlResult = result
        )
    }
}
