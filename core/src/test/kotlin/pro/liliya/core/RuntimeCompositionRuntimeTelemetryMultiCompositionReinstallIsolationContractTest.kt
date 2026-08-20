package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryMultiCompositionReinstallIsolationContractTest {

    @Test
    fun runtime_telemetry_bridge_multi_composition_reinstall_does_not_cross_deliver() {
        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installRuntimeObserverBridge()
        second.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstBefore = first.telemetryObserver()
            .snapshot()

        val secondBefore = second.telemetryObserver()
            .snapshot()

        first.stopRuntimeBridges()

        first.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val firstAfter = first.telemetryObserver()
            .snapshot()

        val secondAfter = second.telemetryObserver()
            .snapshot()

        assertEquals(
            2,
            firstAfter.eventCount
        )

        assertEquals(
            2,
            secondAfter.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            firstAfter.lastEvent
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            secondAfter.lastEvent
        )

        assertEquals(
            firstBefore.startedAt,
            firstAfter.startedAt
        )

        assertEquals(
            secondBefore.startedAt,
            secondAfter.startedAt
        )

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
