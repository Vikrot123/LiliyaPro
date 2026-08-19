package pro.liliya.core

import pro.liliya.core.ModuleEventBus

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeDuplicateDeliveryContractTest {

    @Test
    fun module_failed_event_is_not_duplicated_when_bridge_install_called_twice() {
        val composition = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.subscribe { event ->
            events.add(event)
        }

        composition.installModuleEventBridge()
        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "test-module",
                phase = "start",
                reason = "failure"
            )
        )

        val failures =
            events.filterIsInstance<RuntimeEvent.ModuleFailed>()

        assertEquals(1, failures.size)
    }
    @AfterTest
    fun cleanup() {
        ModuleEventBus.clear()
        RuntimeEventBus.clear()
    }

}
