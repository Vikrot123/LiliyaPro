package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeDuplicateDeliveryContractTest {

    @Test
    fun recovery_failed_event_is_not_duplicated_when_bridge_install_called_twice() {
        val composition = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.subscribe { event ->
            events.add(event)
        }

        composition.installRuntimeRecoveryEventBridge()
        composition.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service"
            )
        )

        val failures =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(1, failures.size)
    }
}
