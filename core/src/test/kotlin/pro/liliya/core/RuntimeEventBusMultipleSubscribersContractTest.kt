package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuntimeEventBusMultipleSubscribersContractTest {

    @Test
    fun eventBusNotifiesAllSubscribers() {

        val first =
            mutableListOf<RuntimeEvent>()

        val second =
            mutableListOf<RuntimeEvent>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            first.add(it)
        }

        RuntimeEventBus.subscribe {
            second.add(it)
        }

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeStarting
        )

        assertEquals(
            1,
            first.size
        )

        assertEquals(
            1,
            second.size
        )

        assertEquals(
            RuntimeEvent.RuntimeStarting,
            first[0]
        )

        assertEquals(
            RuntimeEvent.RuntimeStarting,
            second[0]
        )

        RuntimeEventBus.clear()
    }
}
