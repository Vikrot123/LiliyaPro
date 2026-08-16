package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreRuntimeState

class DefaultRuntimeLifecycleController(
    private val composition: RuntimeLifecycleComposition
) : RuntimeLifecycleController {

    override fun start() {
        if (composition.runtimeState() == CoreRuntimeState.RUNNING ||
            composition.runtimeState() == CoreRuntimeState.STARTING
        ) {
            return
        }

        try {
            composition.startRuntime()
            composition.handleRuntimeStartupSuccess()
            composition.logRuntimeStartupSuccess()
        } catch (error: Exception) {
            composition.logRuntimeStartupFailure(error)
            composition.handleRuntimeStartupFailure(error)
            throw error
        }
    }

    override fun stop() {
        if (composition.runtimeState() == CoreRuntimeState.STOPPED) {
            return
        }

        composition.stopRuntime()
        composition.logRuntimeStopped()
    }
}
