package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ModuleLifecycleStopEventContractTest {

    @Test
    fun stoppingRuntimeMustPublishStoppedEventAfterStarted() {

        val received = mutableListOf<ModuleEvent>()

        ModuleEventBus.clear()

        ModuleEventBus.subscribe {
            received.add(it)
        }

        CoreRuntime.stop()

        CoreRuntime.start()

        received.clear()

        CoreRuntime.stop()

        val stoppedIndex = received.indexOfFirst {
            it is ModuleEvent.Stopped
        }

        assertTrue(
            stoppedIndex >= 0,
            "Stopped event must exist"
        )

        ModuleEventBus.clear()
    }
}
