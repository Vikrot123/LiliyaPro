package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeIsolationContractTest {

    @Test
    fun recovery_event_is_delivered_once_after_bridge_restart() {

        RuntimeEventBus.clear()
        RuntimeRecoveryEventBus.clear()

        val recovered = mutableListOf<RuntimeEvent.RuntimeServiceRecovered>()

        RuntimeEventBus.subscribe { event ->
            if (event is RuntimeEvent.RuntimeServiceRecovered) {
                recovered += event
            }
        }

        val composition =
            DefaultRuntimeComposition()

        composition.installRuntimeRecoveryEventBridge()

        composition.uninstallRuntimeRecoveryEventBridge()

        composition.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Completed("service")
        )

        assertEquals(
            1,
            recovered.size
        )
    }

    @AfterTest
    fun cleanup() {
        RuntimeEventBus.clear()
        RuntimeRecoveryEventBus.clear()
    }
}
