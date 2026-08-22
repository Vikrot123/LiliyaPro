package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBridgeUninstallIsolationContractTest {

    @Test
    fun uninstall_stops_recovery_event_forwarding() {
        val recoveryBus = RuntimeRecoveryEventBus()
        val bridge = RuntimeRecoveryEventBridge(recoveryBus)

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        bridge.install()
        bridge.uninstall()

        recoveryBus.publish(
            RuntimeRecoveryEvent.Completed("after-uninstall-service")
        )

        assertEquals(
            emptyList<RuntimeEvent>(),
            events
        )

        RuntimeEventBus.unsubscribe(listener)
    }
}
