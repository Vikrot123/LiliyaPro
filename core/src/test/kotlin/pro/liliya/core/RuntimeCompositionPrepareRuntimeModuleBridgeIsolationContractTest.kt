package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionPrepareRuntimeModuleBridgeIsolationContractTest {

    @Test
    fun prepare_runtime_clears_module_event_bridge_state() {
        val composition = DefaultRuntimeComposition()

        composition.installModuleEventBridge()

        assertTrue(
            composition
                .runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        composition.prepareRuntime()

        assertFalse(
            composition
                .runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        composition.installModuleEventBridge()

        assertTrue(
            composition
                .runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        composition.uninstallModuleEventBridge()
    }
}
