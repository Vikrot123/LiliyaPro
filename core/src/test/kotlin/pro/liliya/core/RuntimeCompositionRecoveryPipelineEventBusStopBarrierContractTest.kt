package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent

class RuntimeCompositionRecoveryPipelineEventBusStopBarrierContractTest {

    @Test
    fun stopped_composition_recovery_event_bus_does_not_leak_after_stop() {

        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        var recovered = 0
        val listener: (RuntimeEvent) -> Unit = { event ->
            if (event is RuntimeEvent.RuntimeServiceRecovered) {
                recovered++
            }
        }

        RuntimeEventBus.subscribe(listener)

        composition.prepareRuntime()
        composition.installRuntimeRecoveryEventBridge()

        composition.startRuntime()

        composition.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Completed(
                serviceName = "before-stop"
            )
        )

        assertEquals(1, recovered)

        composition.stopRuntime()

        val before = recovered

        composition.recoveryEventBus.publish(
            RuntimeRecoveryEvent.Completed(
                serviceName = "after-stop"
            )
        )

        assertEquals(
            before,
            recovered
        )

        RuntimeEventBus.unsubscribe(listener)
        RuntimeEventBus.clear()
    }
}
