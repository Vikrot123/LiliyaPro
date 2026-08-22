package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*
import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class RuntimeRecoveryEventBridgeDuplicateDeliveryIsolationContractTest {

    @Test
    fun repeated_install_does_not_duplicate_recovery_event_delivery() {
        val recoveryBus = RuntimeRecoveryEventBus()
        val bridge = RuntimeRecoveryEventBridge(recoveryBus)

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        bridge.install()
        bridge.install()

        recoveryBus.publish(
            RuntimeRecoveryEvent.Completed("duplicate-install-service")
        )

        assertEquals(
            listOf<RuntimeEvent>(
                RuntimeEvent.RuntimeServiceRecovered(
                    "duplicate-install-service"
                )
            ),
            events
        )

        RuntimeEventBus.unsubscribe(listener)
    }
}
