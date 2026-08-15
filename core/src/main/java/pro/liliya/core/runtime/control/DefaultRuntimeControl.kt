package pro.liliya.core.runtime.control

import pro.liliya.core.runtime.composition.RuntimeComposition

class DefaultRuntimeControl(
    private val runtimeComposition: RuntimeComposition
) : RuntimeControl {

    override fun execute(
        command: RuntimeCommand
    ): RuntimeControlResult {

        val previousState =
            runtimeComposition.runtimeState()

        val status =
            runtimeComposition.createRuntimeStatus(
                report = runtimeComposition.createHealthReport(
                    state = runtimeComposition.runtimeState(),
                    telemetry = runtimeComposition.telemetryObserver().snapshot(),
                    failure = runtimeComposition.failureTracker().snapshot(),
                    recovery = runtimeComposition.recoveryTracker().snapshot()
                )
            )

        return when (command) {

            RuntimeCommand.HEALTH_CHECK -> {
                RuntimeControlResult(
                    command = command,
                    success = true,
                    previousState = previousState,
                    currentState = runtimeComposition.runtimeState(),
                    status = status,
                    message = "Runtime health check completed"
                )
            }

            else -> {
                RuntimeControlResult(
                    command = command,
                    success = false,
                    previousState = previousState,
                    currentState = runtimeComposition.runtimeState(),
                    status = status,
                    message = "Command not implemented yet"
                )
            }
        }
    }
}
