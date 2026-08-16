package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreRuntimeState

interface RuntimeLifecycleComposition {

    fun runtimeState(): CoreRuntimeState

    fun startRuntime()

    fun stopRuntime()

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
