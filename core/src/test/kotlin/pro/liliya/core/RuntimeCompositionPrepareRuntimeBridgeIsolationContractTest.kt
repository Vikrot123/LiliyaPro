package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionPrepareRuntimeBridgeIsolationContractTest {

    @Test
    fun prepare_runtime_clears_runtime_observer_bridge_state() {
        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        assertTrue(
            composition.isRuntimeObserverBridgeInstalled()
        )

        composition.prepareRuntime()

        assertFalse(
            composition.isRuntimeObserverBridgeInstalled()
        )

        composition.installRuntimeObserverBridge()

        assertTrue(
            composition.isRuntimeObserverBridgeInstalled()
        )

        composition.stopRuntimeBridges()
    }
}
