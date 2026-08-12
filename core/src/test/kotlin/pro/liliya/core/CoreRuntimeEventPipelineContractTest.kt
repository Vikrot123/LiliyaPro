package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeEventPipelineContractTest {

    @Test
    fun successfulRuntimeStartPublishesLifecycleEvents() {

        val received = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add(it)
        }

        CoreRuntime.stop()

        CoreRuntime.start()

        assertEquals(
            3,
            received.size
        )

        assertEquals(
            RuntimeEvent.SystemStart,
            received[0]
        )

        assertEquals(
            RuntimeEvent.RuntimeStarting,
            received[1]
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            received[2]
        )

        CoreRuntime.stop()

        RuntimeEventBus.clear()
    }


    @Test
    fun runtimeStopPublishesSystemStopEvent() {

        val received = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add(it)
        }

        CoreRuntime.stop()
        CoreRuntime.start()

        received.clear()

        CoreRuntime.stop()

        assertEquals(
            1,
            received.size
        )

        assertEquals(
            RuntimeEvent.SystemStop,
            received[0]
        )

        RuntimeEventBus.clear()
    }
}
