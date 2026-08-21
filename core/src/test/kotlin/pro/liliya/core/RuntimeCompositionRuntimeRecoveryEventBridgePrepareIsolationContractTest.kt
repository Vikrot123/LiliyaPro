package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgePrepareIsolationContractTest {

    @Test
    fun recovery_failed_event_is_not_duplicated_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = { event ->
            events.add(event)
        }

        RuntimeEventBus.subscribe(listener)

        composition.installRuntimeRecoveryEventBridge()

        composition.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service"
            )
        )

        composition.prepareRuntime()

        composition.installRuntimeRecoveryEventBridge()

        composition.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service"
            )
        )

        val failures =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(2, failures.size)

        RuntimeEventBus.unsubscribe(listener)
    }
    @AfterTest
    fun cleanup() {
        
        RuntimeEventBus.clear()
    }


}
