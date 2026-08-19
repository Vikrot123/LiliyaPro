package pro.liliya.core.runtime.module

import pro.liliya.core.ModuleEvent
import pro.liliya.core.ModuleEventBus
import pro.liliya.core.runtime.composition.RuntimeComposition

class ModuleEventBridge(
    private val composition: RuntimeComposition
) {

    companion object {
        private var activeBridge: ModuleEventBridge? = null
    }

    private var installed = false

    private val moduleListener: (ModuleEvent) -> Unit = { event ->
        if (event is ModuleEvent.Failed) {
            val failureReason =
                "${event.moduleName}: ${event.phase}: ${event.reason}"

            composition.publishModuleFailed(
                moduleName = event.moduleName,
                reason = failureReason
            )
        }
    }

    fun install() {
        if (installed) {
            return
        }

        activeBridge?.uninstall()

        ModuleEventBus.subscribe(moduleListener)

        activeBridge = this
        installed = true
    }

    fun uninstall() {
        if (!installed) {
            return
        }

        ModuleEventBus.unsubscribe(moduleListener)

        if (activeBridge === this) {
            activeBridge = null
        }

        installed = false
    }
}
