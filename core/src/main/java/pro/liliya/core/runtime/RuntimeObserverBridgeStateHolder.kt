package pro.liliya.core.runtime

class RuntimeObserverBridgeStateHolder {

    private var runtimeObserverBridgeInstalled = false

    fun isRuntimeObserverBridgeInstalled(): Boolean {
        return runtimeObserverBridgeInstalled
    }

    fun markRuntimeObserverBridgeInstalled() {
        runtimeObserverBridgeInstalled = true
    }

    fun reset() {
        runtimeObserverBridgeInstalled = false
    }
}
