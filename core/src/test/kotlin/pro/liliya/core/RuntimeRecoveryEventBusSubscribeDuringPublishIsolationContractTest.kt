package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusSubscribeDuringPublishIsolationContractTest {

    @Test
    fun subscriber_added_during_publish_receives_only_future_events() {
        val bus = RuntimeRecoveryEventBus()

        val eventsA = mutableListOf<RuntimeRecoveryEvent>()
        val eventsB = mutableListOf<RuntimeRecoveryEvent>()

        val listenerB: (RuntimeRecoveryEvent) -> Unit = {
            eventsB.add(it)
        }

        val listenerA: (RuntimeRecoveryEvent) -> Unit = {
            eventsA.add(it)
            bus.subscribe(listenerB)
        }

        bus.subscribe(listenerA)

        bus.publish(
            RuntimeRecoveryEvent.Started("before-subscribe")
        )

        bus.publish(
            RuntimeRecoveryEvent.Completed("after-subscribe")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-subscribe"),
                RuntimeRecoveryEvent.Completed("after-subscribe")
            ),
            eventsA
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Completed("after-subscribe")
            ),
            eventsB
        )
    }
}
