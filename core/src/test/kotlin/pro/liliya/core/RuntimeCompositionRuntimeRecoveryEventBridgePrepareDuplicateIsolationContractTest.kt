package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgePrepareDuplicateIsolationContractTest {

    @Test
    fun recovery_bridge_does_not_duplicate_delivery_after_prepare_runtime() {

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
                serviceName = "before-prepare"
            )
        )

        composition.prepareRuntime()

        composition.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "after-prepare"
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
