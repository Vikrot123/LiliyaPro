package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionModuleEventBridgeOwnershipStateContractTest {

    @Test
    fun replacing_module_event_bridge_clears_previous_composition_state() {
        ModuleEventBus.clear()
        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installModuleEventBridge()

        assertTrue(
            first
                .runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        second.installModuleEventBridge()

        assertFalse(
            first
                .runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        assertTrue(
            second
                .runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()

        ModuleEventBus.clear()
        RuntimeEventBus.clear()
    }
}
