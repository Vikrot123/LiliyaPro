package pro.liliya.core.runtime.module

import pro.liliya.core.ModuleEvent
import pro.liliya.core.ModuleEventBus
import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus
import pro.liliya.core.runtime.composition.RuntimeComposition

class ModuleEventBridge(
    private val composition: RuntimeComposition
) {

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

    private val failureListener: (RuntimeEvent) -> Unit = { event ->
        if (event is RuntimeEvent.ModuleFailed) {
            composition.failureTracker().recordFailure(
                reason = event.reason,
                module = event.moduleName
            )
        }
    }

    fun install() {
        if (installed) {
            return
        }

        ModuleEventBus.subscribe(moduleListener)
        RuntimeEventBus.subscribe(failureListener)

        installed = true
    }

    fun uninstall() {
        if (!installed) {
            return
        }

        ModuleEventBus.unsubscribe(moduleListener)
        RuntimeEventBus.unsubscribe(failureListener)

        installed = false
    }
}
