package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryFailureMultiCompositionIsolationContractTest {

    @Test
    fun runtime_telemetry_failure_multi_composition_isolation_is_preserved() {
        RuntimeEventBus.clear()

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.installRuntimeObserverBridge()
        second.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                "test-module",
                "failure"
            )
        )

        val firstBeforeStop = first.telemetryObserver()
            .snapshot()

        val secondBeforeStop = second.telemetryObserver()
            .snapshot()

        first.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                "test-module",
                "failure"
            )
        )

        val firstAfterStop = first.telemetryObserver()
            .snapshot()

        val secondAfterStop = second.telemetryObserver()
            .snapshot()

        assertEquals(
            firstBeforeStop,
            firstAfterStop
        )

        assertEquals(
            2,
            secondAfterStop.eventCount
        )

        first.stopRuntimeBridges()
        second.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
