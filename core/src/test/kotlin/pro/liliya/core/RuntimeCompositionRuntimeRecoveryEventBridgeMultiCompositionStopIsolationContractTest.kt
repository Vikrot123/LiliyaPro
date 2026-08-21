package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeMultiCompositionStopIsolationContractTest {

    @Test
    fun stopped_composition_does_not_receive_recovery_events() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = { event ->
            events.add(event)
        }

        RuntimeEventBus.subscribe(listener)

        first.installRuntimeRecoveryEventBridge()
        second.installRuntimeRecoveryEventBridge()

        first.stopRuntimeBridges()

        second.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service"
            )
        )

        val failures =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(1, failures.size)

        RuntimeEventBus.unsubscribe(listener)
    }
    @AfterTest
    fun cleanup() {
        
        RuntimeEventBus.clear()
    }


}
