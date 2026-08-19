package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionFullPrepareRestartContractTest {

    @Test
    fun runtime_can_restart_cleanly_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        assertEquals(
            CoreRuntimeState.RUNNING,
            composition.runtimeState()
        )

        composition.stopRuntime()
        composition.prepareRuntime()

        assertEquals(
            CoreRuntimeState.STOPPED,
            composition.runtimeState()
        )

        assertFalse(
            composition.runtimeBridgeStateHolder()
                .isRuntimeObserverBridgeInstalled()
        )

        assertFalse(
            composition.runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        composition.startRuntime()

        assertEquals(
            CoreRuntimeState.RUNNING,
            composition.runtimeState()
        )

        assertTrue(
            composition.runtimeBridgeStateHolder()
                .isRuntimeObserverBridgeInstalled()
        )

        assertTrue(
            composition.runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        composition.stopRuntime()
    }
}
