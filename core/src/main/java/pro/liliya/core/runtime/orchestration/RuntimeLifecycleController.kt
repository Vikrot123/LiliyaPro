package pro.liliya.core.runtime.orchestration

interface RuntimeLifecycleController {

    fun startRuntimeComponents(): pro.liliya.core.module.ModuleManager

    fun startRuntimeLifecycle(): pro.liliya.core.module.ModuleManager

    fun stopRuntimeLifecycle()

    fun startLifecycle()

    fun stopLifecycle()

    fun start()

    fun stop()

    fun logRuntimeStopped()
}
