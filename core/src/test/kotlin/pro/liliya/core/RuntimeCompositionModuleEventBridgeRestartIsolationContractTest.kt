package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeRestartIsolationContractTest {

    @Test
    fun module_event_bridge_does_not_leak_after_runtime_restart() {
        RuntimeEventBus.clear()
        ModuleEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        val composition = DefaultRuntimeComposition()

        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "first",
                phase = "START",
                reason = "failure"
            )
        )

        composition.prepareRuntime()

        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "second",
                phase = "START",
                reason = "failure"
            )
        )

        assertEquals(
            2,
            events.count { it is RuntimeEvent.ModuleFailed }
        )

        RuntimeEventBus.unsubscribe(listener)
        RuntimeEventBus.clear()
        ModuleEventBus.clear()

        composition.stopRuntimeBridges()
    }
}
