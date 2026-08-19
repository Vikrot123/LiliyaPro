package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeStopBarrierContractTest {

    @Test
    fun stop_runtime_bridges_blocks_module_event_delivery() {
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
                moduleName = "before-stop",
                phase = "START",
                reason = "failure"
            )
        )

        composition.stopRuntimeBridges()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "after-stop",
                phase = "START",
                reason = "failure"
            )
        )

        assertEquals(
            1,
            events.count { it is RuntimeEvent.ModuleFailed }
        )

        RuntimeEventBus.unsubscribe(listener)
        RuntimeEventBus.clear()
        ModuleEventBus.clear()
    }
}
