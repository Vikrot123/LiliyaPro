package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.observer.RuntimeObserver

class CoreRuntimeObserverBridgeDuplicateDeliveryContractTest {

    @Test
    fun `runtime ready event is delivered once after restart`() {
        val events = mutableListOf<RuntimeEvent>()

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                events.add(event)
            }
        }

        CoreRuntime.registerRuntimeObserver(observer)

        try {
            CoreRuntime.start()
            CoreRuntime.stop()

            events.clear()

            CoreRuntime.start()

            val readyCount = events.count {
                it == RuntimeEvent.RuntimeReady
            }

            assertEquals(
                1,
                readyCount
            )

        } finally {
            CoreRuntime.stop()
            CoreRuntime.unregisterRuntimeObserver(observer)
        }
    }
}
