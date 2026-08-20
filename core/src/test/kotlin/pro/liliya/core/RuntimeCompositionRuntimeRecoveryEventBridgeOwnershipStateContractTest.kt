package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeOwnershipStateContractTest {

    @Test
    fun replacing_or_stopping_one_recovery_bridge_does_not_corrupt_other_composition_state() {
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installRuntimeRecoveryEventBridge()

        assertTrue(
            first
                .runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        second.installRuntimeRecoveryEventBridge()

        assertTrue(
            first
                .runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        assertTrue(
            second
                .runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        first.uninstallRuntimeRecoveryEventBridge()

        assertFalse(
            first
                .runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        assertTrue(
            second
                .runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        second.uninstallRuntimeRecoveryEventBridge()

        assertFalse(
            second
                .runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()
    }
}
