package pro.liliya.core.runtime.orchestration

interface RuntimeLifecycleComposition {

    fun startRuntimeComponents(): pro.liliya.core.module.ModuleManager

    fun startRuntimeLifecycle(): pro.liliya.core.module.ModuleManager

    fun stopRuntimeLifecycle()

    fun startRuntime()

    fun startLifecycle()

    fun stopLifecycle()

    fun stopRuntime()

    fun start()

    fun stop()

    fun handleRuntimeStartupSuccess()

    fun handleRuntimeStartupFailure(
        error: Exception
    )

    fun logRuntimeStartupSuccess()

    fun logRuntimeStartupFailure(
        error: Exception
    )

    fun logRuntimeStopped()
}
