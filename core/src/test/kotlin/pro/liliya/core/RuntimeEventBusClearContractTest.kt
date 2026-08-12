package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuntimeEventBusClearContractTest {

    @Test
    fun clearRemovesAllSubscribers() {

        val received =
            mutableListOf<RuntimeEvent>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add(it)
        }

        RuntimeEventBus.clear()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            0,
            received.size,
            "Cleared subscribers must not receive events"
        )
    }
}
