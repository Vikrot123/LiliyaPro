package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeCompletedOnlyContractTest {

    @Test
    fun recovery_started_does_not_publish_service_recovered_but_completed_does() {
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.subscribe { event ->
            events.add(event)
        }

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeRecoveryEventBridge()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Started(
                serviceName = "test-service"
            )
        )

        assertEquals(
            0,
            events.filterIsInstance<RuntimeEvent.RuntimeServiceRecovered>().size
        )

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Completed(
                serviceName = "test-service"
            )
        )

        val recovered = events
            .filterIsInstance<RuntimeEvent.RuntimeServiceRecovered>()

        assertEquals(1, recovered.size)
        assertEquals("test-service", recovered.single().serviceName)
    }

    @AfterTest
    fun cleanup() {
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()
    }
}
