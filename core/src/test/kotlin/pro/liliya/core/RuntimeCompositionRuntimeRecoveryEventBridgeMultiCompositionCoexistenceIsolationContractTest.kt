package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeCompositionRuntimeRecoveryEventBridgeMultiCompositionCoexistenceIsolationContractTest {

    @Test
    fun recovery_event_bridge_multi_composition_coexistence_is_isolated() {
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

        val failuresBeforeStop =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(
            2,
            failuresBeforeStop.size
        )

        first.stopRuntimeBridges()

        RuntimeRecoveryEventBus.publish(
            RuntimeRecoveryEvent.Failed(
                serviceName = "second-service"
            )
        )

        val failuresAfterStop =
            events.filterIsInstance<RuntimeEvent.RuntimeServiceFailed>()

        assertEquals(
            3,
            failuresAfterStop.size
        )

        second.stopRuntimeBridges()

        RuntimeEventBus.unsubscribe(listener)
        RuntimeRecoveryEventBus.clear()
        RuntimeEventBus.clear()
    }
}
