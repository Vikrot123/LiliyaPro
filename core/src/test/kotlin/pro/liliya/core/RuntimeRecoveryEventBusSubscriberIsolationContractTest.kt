package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusSubscriberIsolationContractTest {

    @Test
    fun recovery_event_bus_keeps_subscribers_isolated() {
        val busA = RuntimeRecoveryEventBus()
        val busB = RuntimeRecoveryEventBus()

        val eventsA = mutableListOf<RuntimeRecoveryEvent>()
        val eventsB = mutableListOf<RuntimeRecoveryEvent>()

        busA.subscribe {
            eventsA.add(it)
        }

        busB.subscribe {
            eventsB.add(it)
        }

        busA.publish(
            RuntimeRecoveryEvent.Started("service-a")
        )

        busB.publish(
            RuntimeRecoveryEvent.Started("service-b")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                    RuntimeRecoveryEvent.Started("service-a")
                ),
            eventsA
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                    RuntimeRecoveryEvent.Started("service-b")
                ),
            eventsB
        )
    }
}
