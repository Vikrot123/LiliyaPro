package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuntimeEventBusContractTest {

    @Test
    fun eventBusPublishesEventsToSubscribers() {

        val received = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add(it)
        }

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            2,
            received.size
        )

        assertEquals(
            RuntimeEvent.SystemStart,
            received[0]
        )

        assertEquals(
            RuntimeEvent.RuntimeReady,
            received[1]
        )

        RuntimeEventBus.clear()
    }
}
