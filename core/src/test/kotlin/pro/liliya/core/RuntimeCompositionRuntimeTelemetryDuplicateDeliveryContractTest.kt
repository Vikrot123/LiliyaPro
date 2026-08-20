package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryDuplicateDeliveryContractTest {

    @Test
    fun runtime_telemetry_bridge_does_not_duplicate_delivery() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()
        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val snapshot = composition.telemetryObserver()
            .snapshot()

        assertEquals(
            1,
            snapshot.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            snapshot.lastEvent
        )

        composition.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
