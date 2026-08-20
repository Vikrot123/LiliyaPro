package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeFailureMultiCompositionIsolationContractTest {

    @Test
    fun recovery_failure_multi_composition_delivery_is_isolated() {
        RuntimeRecoveryEventBus.clear()
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

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "test-service"
            )
        )

        val failures =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(
            2,
            failures.size
        )

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()

        RuntimeEventBus.unsubscribe(listener)
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()
    }
}
