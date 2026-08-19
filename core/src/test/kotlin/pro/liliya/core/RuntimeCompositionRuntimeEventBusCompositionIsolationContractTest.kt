package pro.liliya.core

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.observer.RuntimeObserver

class RuntimeCompositionRuntimeEventBusCompositionIsolationContractTest {

    @AfterTest
    fun cleanup() {
        RuntimeEventBus.clear()
    }

    @Test
    fun stopped_composition_does_not_receive_runtime_events() {

        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        val firstEvents = mutableListOf<RuntimeEvent>()
        val secondEvents = mutableListOf<RuntimeEvent>()

        val firstObserver = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                firstEvents.add(event)
            }
        }

        val secondObserver = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                secondEvents.add(event)
            }
        }

        first.observerRegistry()
            .subscribe(firstObserver)

        second.observerRegistry()
            .subscribe(secondObserver)

        first.installRuntimeObserverBridge()
        second.installRuntimeObserverBridge()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "test-service",
                reason = "test"
            )
        )

        first.stopRuntimeBridges()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "test-service",
                reason = "test"
            )
        )

        assertEquals(1, firstEvents.size)
        assertEquals(2, secondEvents.size)
    }
}
