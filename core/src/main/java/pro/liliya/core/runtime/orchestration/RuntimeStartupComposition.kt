package pro.liliya.core.runtime.orchestration

interface RuntimeStartupComposition {

    fun runtimeState(): pro.liliya.core.CoreRuntimeState

    fun startupController(): RuntimeStartupController


    fun resetRuntimeHealth()

    fun installRuntimeObserverBridge()

    fun installModuleEventBridge()

    fun publishSystemStart()

    fun publishRuntimeStarting()

    fun startRuntimeLifecycle(): pro.liliya.core.module.ModuleManager

    fun moduleManager(): pro.liliya.core.module.ModuleManager?

    fun stopModuleRuntime(
        manager: pro.liliya.core.module.ModuleManager
    )

    fun setModuleStates(
        states: Map<String, pro.liliya.core.module.ModuleState>
    )

    fun clearModuleRuntime()

    fun markRuntimeFailed(
        reason: String
    )

    fun failureReason(): String?

    fun recordRuntimeFailure(
        reason: String?
    )

    fun publishRuntimeFailedDiagnostic()

    fun publishRuntimeFailed(
        reason: String
    )

    fun recordRuntimeStarted()

    fun publishRuntimeStartedDiagnostic()

    fun markRuntimeRecovered()

    fun publishRuntimeReady()

    fun logRuntimeStartupSuccess()

    fun logRuntimeStartupFailure(
        error: Exception
    )
}
