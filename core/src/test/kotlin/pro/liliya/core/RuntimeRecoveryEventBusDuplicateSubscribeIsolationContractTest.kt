package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusDuplicateSubscribeIsolationContractTest {

    @Test
    fun duplicate_subscribe_does_not_duplicate_event_delivery() {
        val bus = RuntimeRecoveryEventBus()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val listener: (RuntimeRecoveryEvent) -> Unit = {
            events.add(it)
        }

        bus.subscribe(listener)
        bus.subscribe(listener)

        bus.publish(
            RuntimeRecoveryEvent.Started("duplicate-subscribe-service")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("duplicate-subscribe-service")
            ),
            events
        )
    }
}
