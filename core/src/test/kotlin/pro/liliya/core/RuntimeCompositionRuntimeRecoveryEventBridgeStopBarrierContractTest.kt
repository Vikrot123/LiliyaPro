package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeStopBarrierContractTest {

    @Test
    fun stopped_recovery_bridge_blocks_delivery_without_affecting_other_composition() {
        
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installRuntimeRecoveryEventBridge()
        second.installRuntimeRecoveryEventBridge()

        first.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "before-stop"
            )
        )

        assertEquals(
            1,
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>().size
        )

        first.stopRuntimeBridges()

        first.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "after-first-stop"
            )
        )

        assertEquals(
            1,
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>().size
        )

        second.stopRuntimeBridges()

        second.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "after-second-stop"
            )
        )

        assertEquals(
            1,
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>().size
        )

        RuntimeEventBus.unsubscribe(listener)
        
        RuntimeEventBus.clear()
    }
}
