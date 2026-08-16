package pro.liliya.core.runtime.orchestration

import pro.liliya.core.module.ModuleManager

interface RuntimeShutdownComposition {

    fun runtimeState(): pro.liliya.core.CoreRuntimeState

    fun stopRuntimeLifecycle()

    fun moduleManager(): ModuleManager?

    fun setModuleStates(
        states: Map<String, pro.liliya.core.module.ModuleState>
    )

    fun stopModuleRuntime(
        manager: ModuleManager
    )

    fun clearModuleRuntime()

    fun setRuntimeState(
        state: pro.liliya.core.CoreRuntimeState
    )

    fun setFailureReason(
        reason: String?
    )

    fun recordRuntimeStopped()

    fun publishRuntimeStoppedDiagnostic()

    fun publishSystemStop()

    fun stopRuntimeBridges()
}
