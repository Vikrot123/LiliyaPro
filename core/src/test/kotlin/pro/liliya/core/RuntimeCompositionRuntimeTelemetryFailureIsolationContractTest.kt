package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryFailureIsolationContractTest {

    @Test
    fun runtime_telemetry_bridge_failure_delivery_isolated_after_stop() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                "test-module",
                "failure"
            )
        )

        val beforeStop = composition.telemetryObserver()
            .snapshot()

        composition.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                "test-module",
                "failure"
            )
        )

        val afterStop = composition.telemetryObserver()
            .snapshot()

        assertEquals(
            beforeStop,
            afterStop
        )

        composition.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
