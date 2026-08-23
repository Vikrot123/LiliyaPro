package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRecoveryInstallContractTest {

    @Test
    fun composition_installs_recovery_pipeline() {
        val composition = DefaultRuntimeComposition()

        composition.installRuntimeRecoveryEventBridge()

        assertTrue(
            composition
                .runtimeRecoveryEventBridgeStateHolder()
                .isRuntimeRecoveryEventBridgeInstalled()
        )

        composition.uninstallRuntimeRecoveryEventBridge()
    }
}
