package pro.liliya.core.runtime.orchestration

interface RuntimeLifecycleComposition {

    fun startRuntimeComponents(): pro.liliya.core.module.ModuleManager

    fun startRuntimeLifecycle(): pro.liliya.core.module.ModuleManager

    fun stopRuntimeLifecycle()

    fun startRuntime()

    fun stopRuntime()

    fun startLifecycle()

    fun stopLifecycle()

    fun start()

    fun stop()

    fun logRuntimeStopped()
}
