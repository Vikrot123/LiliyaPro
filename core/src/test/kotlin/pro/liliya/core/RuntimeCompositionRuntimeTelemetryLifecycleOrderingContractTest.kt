package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryLifecycleOrderingContractTest {

    @Test
    fun runtime_telemetry_bridge_lifecycle_ordering_is_preserved() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstSnapshot = composition.telemetryObserver()
            .snapshot()

        composition.stopRuntimeBridges()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val secondSnapshot = composition.telemetryObserver()
            .snapshot()

        assertEquals(
            2,
            secondSnapshot.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            firstSnapshot.lastEvent
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            secondSnapshot.lastEvent
        )

        assertEquals(
            firstSnapshot.startedAt,
            secondSnapshot.startedAt
        )

        composition.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
