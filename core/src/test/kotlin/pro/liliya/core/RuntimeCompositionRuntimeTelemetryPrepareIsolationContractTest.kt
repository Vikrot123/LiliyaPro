package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionRuntimeTelemetryPrepareIsolationContractTest {

    @Test
    fun runtime_telemetry_does_not_leak_after_prepare_runtime() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val before =
            composition.telemetryObserver()
                .snapshot()

        assertEquals(
            1,
            before.eventCount
        )

        assertNotNull(
            before.readyAt
        )

        composition.prepareRuntime()

        composition.installRuntimeObserverBridge()

        val afterReset =
                composition.telemetryObserver()
                    .snapshot()

            assertEquals(
                0,
                afterReset.eventCount
            )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        val afterRestart =
            composition.telemetryObserver()
                .snapshot()

        assertEquals(
            1,
            afterRestart.eventCount
        )

        composition.stopRuntimeBridges()

        RuntimeEventBus.clear()
    }
}
