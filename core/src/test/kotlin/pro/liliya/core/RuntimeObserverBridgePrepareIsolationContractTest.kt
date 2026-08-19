package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeObserverBridgePrepareIsolationContractTest {

    @Test
    fun runtime_observer_bridge_state_is_cleared_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.runtimeBridgeStateHolder()
            .markRuntimeObserverBridgeInstalled()

        assertTrue(
            composition.runtimeBridgeStateHolder()
                .isRuntimeObserverBridgeInstalled()
        )

        composition.prepareRuntime()

        assertFalse(
            composition.runtimeBridgeStateHolder()
                .isRuntimeObserverBridgeInstalled()
        )
    }
}
