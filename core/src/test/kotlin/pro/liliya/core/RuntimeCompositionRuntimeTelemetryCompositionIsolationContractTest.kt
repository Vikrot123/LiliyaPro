package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryCompositionIsolationContractTest {

    @Test
    fun runtime_telemetry_bridge_compositions_are_isolated() {
        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installRuntimeObserverBridge()
        second.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstSnapshot = first.telemetryObserver()
            .snapshot()

        val secondSnapshot = second.telemetryObserver()
            .snapshot()

        assertEquals(
            1,
            firstSnapshot.eventCount
        )

        assertEquals(
            1,
            secondSnapshot.eventCount
        )

        first.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstAfterStop = first.telemetryObserver()
            .snapshot()

        val secondAfterStop = second.telemetryObserver()
            .snapshot()

        assertEquals(
            1,
            firstAfterStop.eventCount
        )

        assertEquals(
            2,
            secondAfterStop.eventCount
        )

        RuntimeEventBus.clear()
    }
}
