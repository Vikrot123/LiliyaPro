package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryRestartIsolationContractTest {

    @Test
    fun runtime_telemetry_bridge_restart_does_not_duplicate_updates() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val beforeRestart =
            composition.telemetryObserver()
                .snapshot()

        composition.uninstallRuntimeObserverBridge()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val afterRestart =
            composition.telemetryObserver()
                .snapshot()

        assertEquals(
            2,
            afterRestart.eventCount
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            afterRestart.lastEvent
        )

        assertEquals(
            beforeRestart.startedAt,
            afterRestart.startedAt
        )

        composition.uninstallRuntimeObserverBridge()

        RuntimeEventBus.clear()
    }
}
