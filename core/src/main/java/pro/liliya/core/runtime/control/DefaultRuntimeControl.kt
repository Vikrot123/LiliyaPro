package pro.liliya.core.runtime.control

import pro.liliya.core.CoreRuntime

class DefaultRuntimeControl : RuntimeControl {

    override fun execute(
        command: RuntimeCommand
    ): RuntimeControlResult {

        val previousState = CoreRuntime.state()

        return when (command) {

            RuntimeCommand.HEALTH_CHECK -> {
                RuntimeControlResult(
                    command = command,
                    success = true,
                    previousState = previousState,
                    currentState = CoreRuntime.state(),
                    status = CoreRuntime.getRuntimeStatusSnapshot(),
                    message = "Runtime health check completed"
                )
            }

            else -> {
                RuntimeControlResult(
                    command = command,
                    success = false,
                    previousState = previousState,
                    currentState = CoreRuntime.state(),
                    status = CoreRuntime.getRuntimeStatusSnapshot(),
                    message = "Command not implemented yet"
                )
            }
        }
    }
}
