package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeFailurePropagationContractTest {

    @Test
    fun module_event_failure_propagates_once_through_runtime_pipeline() {
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
                moduleName = "pipeline-test",
                phase = "START",
                reason = "failure"
            )
        )

        assertEquals(
            1,
            events.count { it is RuntimeEvent.ModuleFailed }
        )

        composition.prepareRuntime()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "after-reset",
                phase = "START",
                reason = "failure"
            )
        )

        assertEquals(
            1,
            events.count { it is RuntimeEvent.ModuleFailed }
        )

        composition.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "after-reinstall",
                phase = "START",
                reason = "failure"
            )
        )

        assertEquals(
            2,
            events.count { it is RuntimeEvent.ModuleFailed }
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
            2,
            events.count { it is RuntimeEvent.ModuleFailed }
        )

        RuntimeEventBus.unsubscribe(listener)
        RuntimeEventBus.clear()
        ModuleEventBus.clear()
    }
}
