package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuntimeEventBusLifecycleIsolationContractTest {

    @Test
    fun clearMustRemoveAllRuntimeListeners() {

        val received = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add(it)
        }

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )

        assertEquals(
            1,
            received.size
        )

        RuntimeEventBus.clear()

        RuntimeEventBus.publish(
            RuntimeEvent.SystemStop
        )

        assertEquals(
            1,
            received.size
        )
    }
}
