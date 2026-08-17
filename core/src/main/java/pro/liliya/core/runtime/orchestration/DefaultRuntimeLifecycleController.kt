package pro.liliya.core.runtime.orchestration

class DefaultRuntimeLifecycleController(
    private val composition: RuntimeLifecycleComposition
) : RuntimeLifecycleController {

    override fun start() {
        composition.startRuntime()
    }

    override fun stop() {
        composition.stopRuntime()
    }


    override fun startRuntimeComponents(): pro.liliya.core.module.ModuleManager {
        return composition.startRuntimeComponents()
    }

    override fun startRuntimeLifecycle(): pro.liliya.core.module.ModuleManager {
        return composition.startRuntimeLifecycle()
    }

    override fun stopRuntimeLifecycle() {
        composition.stopRuntimeLifecycle()
    }

    override fun startLifecycle() {
        composition.startLifecycle()
    }

    override fun stopLifecycle() {
        composition.stopLifecycle()
    }

    override fun logRuntimeStopped() {
        composition.logRuntimeStopped()
    }
}
