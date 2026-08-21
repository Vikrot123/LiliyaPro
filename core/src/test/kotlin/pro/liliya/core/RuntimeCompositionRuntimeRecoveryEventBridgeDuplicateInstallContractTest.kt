package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeDuplicateInstallContractTest {

    @Test
    fun duplicate_install_does_not_duplicate_recovery_delivery() {

        RuntimeEventBus.clear()
        

        val recovered = mutableListOf<RuntimeEvent.RuntimeServiceRecovered>()

        RuntimeEventBus.subscribe { event ->
            if (event is RuntimeEvent.RuntimeServiceRecovered) {
                recovered += event
            }
        }

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeRecoveryEventBridge()
        composition.installRuntimeRecoveryEventBridge()

        composition.recoveryEventBus.publish(
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
        
    }
}
