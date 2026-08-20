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
            composition.runtimeObserverBridgeStateHolder()
                .isRuntimeObserverBridgeInstalled()
        )

        assertFalse(
            composition.runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        assertFalse(
            composition.runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        composition.startRuntime()

        assertEquals(
            CoreRuntimeState.RUNNING,
            composition.runtimeState()
        )

        assertTrue(
            composition.runtimeObserverBridgeStateHolder()
                .isRuntimeObserverBridgeInstalled()
        )

        assertTrue(
            composition.runtimeModuleBridgeStateHolder()
                .isModuleEventBridgeInstalled()
        )

        assertTrue(
            composition.runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        composition.stopRuntime()
    }
}
