package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryBridgeReinstallIsolationContractTest {

    @Test
    fun runtime_telemetry_bridge_reinstall_after_stop_does_not_duplicate_delivery() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val beforeStop = composition.telemetryObserver()
            .snapshot()

        composition.stopRuntimeBridges()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val afterReinstall = composition.telemetryObserver()
            .snapshot()

        assertEquals(
            2,
            afterReinstall.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            afterReinstall.lastEvent
        )

        assertEquals(
            beforeStop.startedAt,
            afterReinstall.startedAt
        )

        composition.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
