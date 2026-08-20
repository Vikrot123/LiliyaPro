package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeFailureRestartMultiCompositionIsolationContractTest {

    @Test
    fun module_event_bridge_failure_restart_multi_composition_isolation_is_preserved() {
        ModuleEventBus.clear()
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installModuleEventBridge()
        second.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "test-module",
                phase = "START",
                reason = "failure"
            )
        )

        val beforeRestart =
            events.filterIsInstance<RuntimeEvent.ModuleFailed>()

        second.uninstallModuleEventBridge()
        second.installModuleEventBridge()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "test-module",
                phase = "START",
                reason = "failure-after-restart"
            )
        )

        val afterRestart =
            events.filterIsInstance<RuntimeEvent.ModuleFailed>()

        assertEquals(
            2,
            beforeRestart.size + (afterRestart.size - beforeRestart.size)
        )

        assertEquals(
            2,
            events.filterIsInstance<RuntimeEvent.ModuleFailed>().size
        )

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()

        RuntimeEventBus.unsubscribe(listener)
        RuntimeEventBus.clear()
        ModuleEventBus.clear()
    }
}
