package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeMultiCompositionCoexistenceIsolationContractTest {

    @Test
    fun recovery_event_bridge_multi_composition_coexistence_is_isolated() {
        
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
                serviceName = "test-service"
            )
        )

        val failuresBeforeStop =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(
            1,
            failuresBeforeStop.size
        )

        first.stopRuntimeBridges()

        first.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "second-service"
            )
        )

        val failuresAfterStop =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(
            1,
            failuresAfterStop.size
        )

        second.stopRuntimeBridges()

        RuntimeEventBus.unsubscribe(listener)
        
        RuntimeEventBus.clear()
    }
}
