package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuntimeEventBusDuplicateSubscriptionContractTest {

    @Test
    fun sameListenerSubscribedTwiceMustReceiveEventTwice() {

        val received = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            received.add(it)
        }

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe(listener)
        RuntimeEventBus.subscribe(listener)

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )

        assertEquals(
            2,
            received.size
        )

        RuntimeEventBus.clear()
    }
}
