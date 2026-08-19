package pro.liliya.core

import pro.liliya.core.ModuleEventBus

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgePrepareIsolationContractTest {

    @Test
    fun module_failed_event_is_not_duplicated_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = { event ->
            events.add(event)
        }

        RuntimeEventBus.subscribe(listener)

        composition.installModuleEventBridge()

        composition.prepareRuntime()

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

        RuntimeEventBus.unsubscribe(listener)
    }
    @AfterTest
    fun cleanup() {
        ModuleEventBus.clear()
        RuntimeEventBus.clear()
    }

}
