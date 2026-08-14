package pro.liliya.core

import pro.liliya.core.module.ModuleState

class CoreRuntimeStateHolder {

    private var currentState: CoreRuntimeState = CoreRuntimeState.STOPPED

    private var lastFailureReason: String? = null

    private var lastModuleStates: Map<String, ModuleState> = emptyMap()

    fun state(): CoreRuntimeState {
        return currentState
    }

    fun setState(state: CoreRuntimeState) {
        currentState = state
    }

    fun failureReason(): String? {
        return lastFailureReason
    }

    fun setFailureReason(reason: String?) {
        lastFailureReason = reason
    }

    fun moduleStates(): Map<String, ModuleState> {
        return lastModuleStates
    }

    fun setModuleStates(states: Map<String, ModuleState>) {
        lastModuleStates = states
    }

    fun reset() {
        currentState = CoreRuntimeState.STOPPED
        lastFailureReason = null
        lastModuleStates = emptyMap()
    }
}
