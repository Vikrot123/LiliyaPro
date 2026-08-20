package pro.liliya.core.runtime

class RuntimeRecoveryEventBridgeStateHolder {

    private var runtimeRecoveryEventBridgeInstalled = false

    fun isRuntimeRecoveryEventBridgeInstalled(): Boolean {
        return runtimeRecoveryEventBridgeInstalled
    }

    fun markRuntimeRecoveryEventBridgeInstalled() {
        runtimeRecoveryEventBridgeInstalled = true
    }

    fun reset() {
        runtimeRecoveryEventBridgeInstalled = false
    }
}
