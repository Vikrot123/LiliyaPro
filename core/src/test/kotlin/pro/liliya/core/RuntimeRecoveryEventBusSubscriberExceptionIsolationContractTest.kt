package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusSubscriberExceptionIsolationContractTest {

    @Test
    fun failing_subscriber_does_not_break_other_subscribers() {
        val bus = RuntimeRecoveryEventBus()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val failingListener: (RuntimeRecoveryEvent) -> Unit = {
            throw IllegalStateException("subscriber failure")
        }

        val healthyListener: (RuntimeRecoveryEvent) -> Unit = {
            events.add(it)
        }

        bus.subscribe(failingListener)
        bus.subscribe(healthyListener)

        bus.publish(
            RuntimeRecoveryEvent.Started("exception-isolation-service")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("exception-isolation-service")
            ),
            events
        )
    }
}
