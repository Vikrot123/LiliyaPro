package pro.liliya.core.runtime.control

import pro.liliya.core.runtime.action.RuntimeActionRequest

import pro.liliya.core.runtime.composition.RuntimeComposition

class DefaultRuntimeControl(
    private val runtimeComposition: RuntimeComposition
) : RuntimeControl {

    override fun execute(
        request: RuntimeActionRequest
    ): RuntimeControlResult {

        val command = request.command

        val previousState =
            runtimeComposition.runtimeState()

        val status =
            runtimeComposition.runtimeStatusSnapshot()

        return when (command) {

            RuntimeCommand.START -> {
                runtimeComposition.startRuntime()

                RuntimeControlResult(
                    command = command,
                    success = true,
                    previousState = previousState,
                    currentState = runtimeComposition.runtimeState(),
                    status = status,
                    message = "Runtime start completed"
                )
            }

            RuntimeCommand.STOP -> {
                runtimeComposition.stopRuntime()

                RuntimeControlResult(
                    command = command,
                    success = true,
                    previousState = previousState,
                    currentState = runtimeComposition.runtimeState(),
                    status = status,
                    message = "Runtime stop completed"
                )
            }

            RuntimeCommand.RESTART -> {
                runtimeComposition.stopRuntime()
                runtimeComposition.startRuntime()

                RuntimeControlResult(
                    command = command,
                    success = true,
                    previousState = previousState,
                    currentState = runtimeComposition.runtimeState(),
                    status = status,
                    message = "Runtime restart completed"
                )
            }


              RuntimeCommand.RECOVER -> {
                  val recovered =
                      runtimeComposition
                          .runtimeRecoveryManager()
                          .recover(request.target ?: "runtime")

                  RuntimeControlResult(
                      command = command,
                      success = recovered,
                      previousState = previousState,
                      currentState = runtimeComposition.runtimeState(),
                      status = status,
                      message =
                          if (recovered) {
                              "Runtime recovery completed"
                          } else {
                              "Runtime recovery failed"
                          }
                  )
              }

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
