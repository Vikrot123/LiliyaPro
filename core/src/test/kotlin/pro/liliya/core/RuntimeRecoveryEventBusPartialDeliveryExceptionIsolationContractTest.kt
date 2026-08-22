package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusPartialDeliveryExceptionIsolationContractTest {

    @Test
    fun exception_after_partial_delivery_does_not_break_remaining_subscribers() {
        val bus = RuntimeRecoveryEventBus()

        val events = mutableListOf<String>()

        val first: (RuntimeRecoveryEvent) -> Unit = {
            events.add("first")
        }

        val failing: (RuntimeRecoveryEvent) -> Unit = {
            events.add("failing")
            throw IllegalStateException("expected subscriber failure")
        }

        val last: (RuntimeRecoveryEvent) -> Unit = {
            events.add("last")
        }

        bus.subscribe(first)
        bus.subscribe(failing)
        bus.subscribe(last)

        bus.publish(
            RuntimeRecoveryEvent.Started("partial-delivery")
        )

        assertEquals(
            listOf(
                "first",
                "failing",
                "last"
            ),
            events
        )
    }
}
