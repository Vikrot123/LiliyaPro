package pro.liliya.core

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.observer.RuntimeObserver

class ModuleEventBridgeLifecycleContractTest {

    @Test
    fun moduleEventBridgeCanRestartWithoutDuplicateDelivery() {
        RuntimeEventBus.clear()
        ModuleEventBus.clear()

        val composition = DefaultRuntimeComposition()

        val received = mutableListOf<RuntimeEvent>()

        composition.observerRegistry().subscribe(
            object : RuntimeObserver {
                override fun onRuntimeEvent(event: RuntimeEvent) {
                    received.add(event)
                }
            }
        )

        composition.installRuntimeObserverBridge()
        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "test-module",
                phase = "start",
                reason = "failure"
            )
        )

        composition.uninstallModuleEventBridge()

        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "test-module",
                phase = "start",
                reason = "failure"
            )
        )

        assertEquals(
            2,
            received.count { it is RuntimeEvent.ModuleFailed }
        )

        composition.uninstallModuleEventBridge()
        composition.uninstallRuntimeObserverBridge()

        RuntimeEventBus.clear()
        ModuleEventBus.clear()
    }
}
