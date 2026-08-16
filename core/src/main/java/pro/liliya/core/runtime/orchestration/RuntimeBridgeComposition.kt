package pro.liliya.core.runtime.orchestration

interface RuntimeBridgeComposition {

    fun installRuntimeObserverBridge()

    fun installModuleEventBridge()

    fun uninstallModuleEventBridge()

    fun stopRuntimeBridges()
}
