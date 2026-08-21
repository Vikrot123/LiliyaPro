package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusUnsubscribeIsolationContractTest {

    @Test
    fun unsubscribe_removes_only_target_recovery_event_subscriber() {
        val bus = RuntimeRecoveryEventBus()

        val eventsA = mutableListOf<RuntimeRecoveryEvent>()
        val eventsB = mutableListOf<RuntimeRecoveryEvent>()

        val listenerA: (RuntimeRecoveryEvent) -> Unit = {
            eventsA.add(it)
        }

        val listenerB: (RuntimeRecoveryEvent) -> Unit = {
            eventsB.add(it)
        }

        bus.subscribe(listenerA)
        bus.subscribe(listenerB)

        bus.publish(
            RuntimeRecoveryEvent.Started("before-unsubscribe")
        )

        bus.unsubscribe(listenerA)

        bus.publish(
            RuntimeRecoveryEvent.Completed("after-unsubscribe")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-unsubscribe")
            ),
            eventsA
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-unsubscribe"),
                RuntimeRecoveryEvent.Completed("after-unsubscribe")
            ),
            eventsB
        )
    }
}
