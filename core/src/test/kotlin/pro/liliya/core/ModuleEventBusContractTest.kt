package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ModuleEventBusContractTest {

    @Test
    fun eventBusPublishesModuleEvents() {
        val received = mutableListOf<ModuleEvent>()

        ModuleEventBus.clear()

        ModuleEventBus.subscribe {
            received.add(it)
        }

        ModuleEventBus.publish(
            ModuleEvent.Loaded("CoreModule")
        )

        ModuleEventBus.publish(
            ModuleEvent.Started("CoreModule")
        )

        assertEquals(
            2,
            received.size
        )

        assertEquals(
            ModuleEvent.Loaded("CoreModule"),
            received[0]
        )

        assertEquals(
            ModuleEvent.Started("CoreModule"),
            received[1]
        )

        ModuleEventBus.clear()
    }
}
