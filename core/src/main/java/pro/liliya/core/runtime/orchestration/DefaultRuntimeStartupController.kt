package pro.liliya.core.runtime.orchestration

class DefaultRuntimeStartupController(
    private val composition: RuntimeStartupComposition
) : RuntimeStartupController {

    override fun start() {
        composition.resetRuntimeHealth()

        composition.installRuntimeObserverBridge()
        composition.installModuleEventBridge()

        composition.publishSystemStart()
        composition.publishRuntimeStarting()

        try {
            composition.startRuntimeLifecycle()
            composition.handleRuntimeStartupSuccess()
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
