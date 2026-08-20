package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryMultiCompositionStopIsolationContractTest {

    @Test
    fun stopped_composition_does_not_block_other_runtime_telemetry_updates() {

        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installRuntimeObserverBridge()
        second.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstBeforeStop =
            first.telemetryObserver()
                .snapshot()

        val secondBeforeStop =
            second.telemetryObserver()
                .snapshot()

        first.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstAfterStop =
            first.telemetryObserver()
                .snapshot()

        val secondAfterStop =
            second.telemetryObserver()
                .snapshot()

        assertEquals(
            firstBeforeStop,
            firstAfterStop
        )

        assertEquals(
            1,
            firstAfterStop.eventCount
        )

        assertEquals(
            2,
            secondAfterStop.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            firstAfterStop.lastEvent
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            secondAfterStop.lastEvent
        )

        RuntimeEventBus.clear()
    }
}
