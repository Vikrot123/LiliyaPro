package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeDuplicateDeliveryContractTest {

    @Test
    fun module_event_bridge_does_not_duplicate_delivery_after_reinstall() {
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = { event ->
            events.add(event)
        }

        RuntimeEventBus.subscribe(listener)

        val composition = DefaultRuntimeComposition()

        composition.installModuleEventBridge()
        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "test-module",
                phase = "START",
                reason = "failure"
            )
        )

        assertEquals(
            1,
            events.count {
                it is RuntimeEvent.ModuleFailed
            }
        )

        RuntimeEventBus.unsubscribe(listener)
        RuntimeEventBus.clear()
        composition.stopRuntimeBridges()
    }
}
