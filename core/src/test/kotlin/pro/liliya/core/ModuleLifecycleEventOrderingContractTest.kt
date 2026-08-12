package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ModuleLifecycleEventOrderingContractTest {

    @Test
    fun moduleLifecycleEventsMustFollowCorrectOrder() {

        val received = mutableListOf<ModuleEvent>()

        ModuleEventBus.clear()

        ModuleEventBus.subscribe {
            received.add(it)
        }

        CoreRuntime.stop()

        CoreRuntime.start()

        val lifecycle = received.filter {
            it is ModuleEvent.Loaded ||
            it is ModuleEvent.Initialized ||
            it is ModuleEvent.Started
        }

        assertEquals(
            listOf(
                ModuleEvent.Loaded::class,
                ModuleEvent.Initialized::class,
                ModuleEvent.Started::class
            ),
            lifecycle.map { it::class }
        )

        CoreRuntime.stop()
        ModuleEventBus.clear()
    }
}
