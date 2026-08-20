package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryFailureDuplicateIsolationContractTest {

    @Test
    fun runtime_telemetry_bridge_failure_reinstall_does_not_duplicate_delivery() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                "test-module",
                "failure"
            )
        )

        composition.stopRuntimeBridges()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                "test-module",
                "failure"
            )
        )

        val snapshot = composition.telemetryObserver()
            .snapshot()

        assertEquals(
            2,
            snapshot.eventCount
        )

        composition.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
