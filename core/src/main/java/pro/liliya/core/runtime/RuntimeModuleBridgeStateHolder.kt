package pro.liliya.core.runtime

class RuntimeModuleBridgeStateHolder {

    private var moduleEventBridgeInstalled = false

    fun isModuleEventBridgeInstalled(): Boolean {
        return moduleEventBridgeInstalled
    }

    fun markModuleEventBridgeInstalled() {
        moduleEventBridgeInstalled = true
    }

    fun reset() {
        moduleEventBridgeInstalled = false
    }
}
