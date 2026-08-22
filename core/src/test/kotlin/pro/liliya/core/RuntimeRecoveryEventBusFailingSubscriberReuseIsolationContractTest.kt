package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusFailingSubscriberReuseIsolationContractTest {

    @Test
    fun failing_subscriber_can_be_invoked_again_without_corrupting_bus_state() {
        val bus = RuntimeRecoveryEventBus()

        val calls = mutableListOf<String>()

        val failing: (RuntimeRecoveryEvent) -> Unit = {
            calls.add("failing")
            throw IllegalStateException("expected failure")
        }

        val healthy: (RuntimeRecoveryEvent) -> Unit = {
            calls.add("healthy")
        }

        bus.subscribe(failing)
        bus.subscribe(healthy)

        bus.publish(
            RuntimeRecoveryEvent.Started("first")
        )

        bus.publish(
            RuntimeRecoveryEvent.Completed("second")
        )

        assertEquals(
            listOf(
                "failing",
                "healthy",
                "failing",
                "healthy"
            ),
            calls
        )
    }
}
