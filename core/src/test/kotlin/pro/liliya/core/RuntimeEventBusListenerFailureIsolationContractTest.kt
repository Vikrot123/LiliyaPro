package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuntimeEventBusListenerFailureIsolationContractTest {

    @Test
    fun failedListenerMustNotBlockOtherListeners() {

        val received = mutableListOf<String>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add("BEFORE")
        }

        RuntimeEventBus.subscribe {
            throw RuntimeException("BROKEN_LISTENER")
        }

        RuntimeEventBus.subscribe {
            received.add("AFTER")
        }

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )

        assertEquals(
            listOf(
                "BEFORE",
                "AFTER"
            ),
            received
        )

        RuntimeEventBus.clear()
    }
}
