package pro.liliya.core.runtime.action

import pro.liliya.core.runtime.control.RuntimeControl

class RuntimeActionExecutor(
    private val runtimeControl: RuntimeControl
) {

    fun execute(
        request: RuntimeActionRequest
    ): RuntimeActionResult {

        val result = runtimeControl.execute(
            request.command
        )

        return RuntimeActionResult(
            request = request,
            success = result.success,
            controlResult = result
        )
    }
}
