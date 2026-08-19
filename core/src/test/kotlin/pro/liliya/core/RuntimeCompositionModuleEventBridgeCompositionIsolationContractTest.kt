package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeCompositionIsolationContractTest {

    @Test
    fun module_event_bridge_does_not_leak_between_compositions() {
        ModuleEventBus.clear()
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = { event ->
            events.add(event)
        }

        RuntimeEventBus.subscribe(listener)

        val first = DefaultRuntimeComposition()
        first.installModuleEventBridge()

        val second = DefaultRuntimeComposition()
        second.installModuleEventBridge()

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
        ModuleEventBus.clear()
        RuntimeEventBus.clear()

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()
    }
}
