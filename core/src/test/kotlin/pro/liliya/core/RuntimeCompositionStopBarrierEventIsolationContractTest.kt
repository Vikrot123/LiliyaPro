package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionStopBarrierEventIsolationContractTest {

    @Test
    fun stopped_runtime_composition_must_not_receive_runtime_events() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        val events = mutableListOf<RuntimeEvent>()

        composition.observerRegistry()
            .subscribe(
                object : RuntimeObserver {
                    override fun onRuntimeEvent(event: RuntimeEvent) {
                        events.add(event)
                    }
                }
            )

        composition.prepareRuntime()
        composition.startRuntime()

        events.clear()

        composition.stopRuntime()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "stopped-service",
                reason = "after-stop"
            )
        )

        assertEquals(
            0,
            events.size
        )

        RuntimeEventBus.clear()
    }
}
