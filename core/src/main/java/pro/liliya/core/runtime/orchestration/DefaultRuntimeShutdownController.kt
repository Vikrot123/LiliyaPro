package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreRuntimeState

class DefaultRuntimeShutdownController(
    private val composition: RuntimeShutdownComposition
) : RuntimeShutdownController {

    override fun stop() {
        if (composition.runtimeState() == CoreRuntimeState.STOPPED &&
            composition.moduleManager() == null
        ) {
            return
        }

        composition.stopRuntimeLifecycle()

        composition.recordRuntimeStopped()
        composition.setFailureReason(null)
        composition.setModuleStates(emptyMap())

        composition.publishRuntimeStoppedDiagnostic()
        composition.publishSystemStop()

        composition.stopRuntimeBridges()
    }
}
