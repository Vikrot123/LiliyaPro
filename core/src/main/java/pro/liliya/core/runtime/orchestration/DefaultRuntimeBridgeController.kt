package pro.liliya.core.runtime.orchestration

class DefaultRuntimeBridgeController(
    private val composition: RuntimeBridgeComposition
) : RuntimeBridgeController {

    override fun install() {
        composition.installRuntimeObserverBridge()
        composition.installModuleEventBridge()
    }

    override fun uninstall() {
        composition.uninstallModuleEventBridge()
    }

    override fun stop() {
        composition.stopRuntimeBridges()
    }
}
