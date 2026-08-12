package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuntimeEventBusListenerOrderingContractTest {

    @Test
    fun listenersMustReceiveEventsInSubscriptionOrder() {

        val received = mutableListOf<String>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add("FIRST")
        }

        RuntimeEventBus.subscribe {
            received.add("SECOND")
        }

        RuntimeEventBus.subscribe {
            received.add("THIRD")
        }

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )

        assertEquals(
            listOf(
                "FIRST",
                "SECOND",
                "THIRD"
            ),
            received
        )

        RuntimeEventBus.clear()
    }
}
