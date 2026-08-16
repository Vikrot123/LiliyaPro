package pro.liliya.core.runtime.orchestration

class DefaultRuntimeLifecycleController(
    private val composition: RuntimeLifecycleComposition
) : RuntimeLifecycleController {

    override fun start() {
        composition.startLifecycle()
    }

    override fun stop() {
        composition.stopLifecycle()
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

    override fun startRuntime() {
        composition.startRuntime()
    }

    override fun startLifecycle() {
        composition.startLifecycle()
    }

    override fun stopLifecycle() {
        composition.stopLifecycle()
    }

    override fun stopRuntime() {
        composition.stopRuntime()
    }

    override fun handleRuntimeStartupSuccess() {
        composition.handleRuntimeStartupSuccess()
    }

    override fun handleRuntimeStartupFailure(
        error: Exception
    ) {
        composition.handleRuntimeStartupFailure(
            error = error
        )
    }

    override fun logRuntimeStartupSuccess() {
        composition.logRuntimeStartupSuccess()
    }

    override fun logRuntimeStartupFailure(
        error: Exception
    ) {
        composition.logRuntimeStartupFailure(
            error = error
        )
    }

    override fun logRuntimeStopped() {
        composition.logRuntimeStopped()
    }
}
