package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusSubscriberOrderingContractTest {

    @Test
    fun subscribers_receive_events_in_registration_order() {
        val bus = RuntimeRecoveryEventBus()

        val order = mutableListOf<String>()

        val first: (RuntimeRecoveryEvent) -> Unit = {
            order.add("first")
        }

        val second: (RuntimeRecoveryEvent) -> Unit = {
            order.add("second")
        }

        val third: (RuntimeRecoveryEvent) -> Unit = {
            order.add("third")
        }

        bus.subscribe(first)
        bus.subscribe(second)
        bus.subscribe(third)

        bus.publish(
            RuntimeRecoveryEvent.Started("ordering-service")
        )

        assertEquals(
            listOf(
                "first",
                "second",
                "third"
            ),
            order
        )
    }
}
