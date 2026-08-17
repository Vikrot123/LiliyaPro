package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreRuntimeState

class DefaultRuntimeStartupController(
    private val composition: RuntimeStartupComposition
) : RuntimeStartupController {

    override fun start() {
        if (composition.runtimeState() == CoreRuntimeState.RUNNING) {
            return
        }

        composition.resetRuntimeHealth()

        composition.installRuntimeObserverBridge()
        composition.installModuleEventBridge()

        composition.publishSystemStart()
        composition.prepareRuntimeStartup()
        composition.publishRuntimeStarting()

        try {
            composition.startRuntimeLifecycle()

            composition.recordRuntimeStarted()
            composition.publishRuntimeStartedDiagnostic()
            composition.markRuntimeRecovered()
            composition.publishRuntimeReady()

            composition.logRuntimeStartupSuccess()
        } catch (error: Exception) {

            composition.moduleManager()?.let {
                try {
                    composition.stopModuleRuntime(it)
                } catch (_: Exception) {
                }
            }

            composition.moduleManager()?.let {
                composition.setModuleStates(it.getModuleStates())
            }

            composition.clearModuleRuntime()

            composition.markRuntimeFailed(
                error.message ?: "unknown"
            )

            composition.recordRuntimeFailure(
                composition.failureReason()
            )

            composition.publishRuntimeFailedDiagnostic()

            composition.publishRuntimeFailed(
                error.message ?: "unknown"
            )

            composition.logRuntimeStartupFailure(error)

            throw error
        }
    }
}
