package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.observer.RuntimeObserver

class CoreRuntimeObserverRestartIsolationContractTest {

    @Test
    fun `runtime observer receives isolated restart lifecycle`() {
        val received = mutableListOf<RuntimeEvent>()

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                received.add(event)
            }
        }

        CoreRuntime.registerRuntimeObserver(observer)

        try {
            CoreRuntime.start()

            val firstRunCount = received.size

            CoreRuntime.stop()

            val stoppedCount = received.size

            CoreRuntime.start()

            try {
                assertTrue(firstRunCount > 0)
                assertTrue(stoppedCount >= firstRunCount)

                assertEquals(
                    RuntimeEvent.RuntimeReady,
                    received.last()
                )
            } finally {
                CoreRuntime.stop()
            }

        } finally {
            CoreRuntime.unregisterRuntimeObserver(observer)
        }
    }
}
