package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBridgeRestartIsolationContractTest {

    @Test
    fun reinstall_does_not_duplicate_recovery_event_forwarding() {
        val recoveryBus = RuntimeRecoveryEventBus()
        val bridge = RuntimeRecoveryEventBridge(recoveryBus)

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        bridge.install()

        recoveryBus.publish(
            RuntimeRecoveryEvent.Completed("first-cycle")
        )

        bridge.uninstall()

        bridge.install()

        recoveryBus.publish(
            RuntimeRecoveryEvent.Completed("second-cycle")
        )

        assertEquals(
            listOf(
                "first-cycle",
                "second-cycle"
            ),
            events.map {
                (it as RuntimeEvent.RuntimeServiceRecovered).serviceName
            }
        )

        RuntimeEventBus.unsubscribe(listener)
    }
}
