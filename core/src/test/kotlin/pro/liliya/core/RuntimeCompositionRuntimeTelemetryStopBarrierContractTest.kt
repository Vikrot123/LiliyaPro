package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryStopBarrierContractTest {

    @Test
    fun stop_runtime_bridges_blocks_runtime_telemetry_updates() {

        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val beforeStop =
            composition.telemetryObserver()
                .snapshot()

        composition.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val afterStop =
            composition.telemetryObserver()
                .snapshot()

        assertEquals(
            beforeStop,
            afterStop
        )

        RuntimeEventBus.clear()
    }
}
