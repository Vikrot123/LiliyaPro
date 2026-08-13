package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.observer.RuntimeObserver

class CoreRuntimeObserverPipelineContractTest {

    @Test
    fun `runtime observer receives lifecycle events`() {
        val received = mutableListOf<RuntimeEvent>()

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                received.add(event)
            }
        }

        CoreRuntime.registerRuntimeObserver(observer)

        try {
            CoreRuntime.start()

            println("RECEIVED EVENTS = $received")

            assertTrue(
                received.contains(RuntimeEvent.SystemStart)
            )

            assertTrue(
                received.contains(RuntimeEvent.RuntimeStarting)
            )

            assertTrue(
                received.contains(RuntimeEvent.RuntimeReady)
            )
        } finally {
            CoreRuntime.unregisterRuntimeObserver(observer)
            CoreRuntime.stop()
        }
    }
}
