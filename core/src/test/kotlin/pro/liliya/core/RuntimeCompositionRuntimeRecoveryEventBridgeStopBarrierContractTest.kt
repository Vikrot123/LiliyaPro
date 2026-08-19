package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeStopBarrierContractTest {

    @Test
    fun recovery_event_is_blocked_after_runtime_bridge_stop() {
        val composition = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = { event ->
            events.add(event)
        }

        RuntimeEventBus.subscribe(listener)

        composition.installRuntimeRecoveryEventBridge()

        composition.stopRuntimeBridges()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service"
            )
        )

        val failures =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(0, failures.size)

        RuntimeEventBus.unsubscribe(listener)
    }
    @AfterTest
    fun cleanup() {
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()
    }


}
