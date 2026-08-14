package pro.liliya.core.runtime

class RuntimeBridgeStateHolder {

    private var runtimeObserverBridgeInstalled = false
    private var moduleEventBridgeInstalled = false

    fun isRuntimeObserverBridgeInstalled(): Boolean {
        return runtimeObserverBridgeInstalled
    }

    fun markRuntimeObserverBridgeInstalled() {
        runtimeObserverBridgeInstalled = true
    }

    fun isModuleEventBridgeInstalled(): Boolean {
        return moduleEventBridgeInstalled
    }

    fun markModuleEventBridgeInstalled() {
        moduleEventBridgeInstalled = true
    }

    fun reset() {
        runtimeObserverBridgeInstalled = false
        moduleEventBridgeInstalled = false
    }
}
