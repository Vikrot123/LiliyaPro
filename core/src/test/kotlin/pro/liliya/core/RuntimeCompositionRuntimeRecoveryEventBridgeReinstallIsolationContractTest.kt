package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeReinstallIsolationContractTest {

    @Test
    fun recovery_bridge_reinstall_does_not_duplicate_delivery() {

        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "before-reinstall"
            )
        )

        composition.uninstallRuntimeRecoveryEventBridge()

        composition.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "after-reinstall"
            )
        )

        val failures =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(2, failures.size)

        composition.stopRuntimeBridges()

        RuntimeEventBus.unsubscribe(listener)
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()
    }
}
