package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusUnsubscribeOtherDuringPublishIsolationContractTest {

    @Test
    fun unsubscribe_other_listener_during_publish_keeps_current_snapshot_delivery() {
        val bus = RuntimeRecoveryEventBus()

        val eventsA = mutableListOf<RuntimeRecoveryEvent>()
        val eventsB = mutableListOf<RuntimeRecoveryEvent>()

        lateinit var listenerB: (RuntimeRecoveryEvent) -> Unit

        val listenerA: (RuntimeRecoveryEvent) -> Unit = {
            eventsA.add(it)
            bus.unsubscribe(listenerB)
        }

        listenerB = {
            eventsB.add(it)
        }

        bus.subscribe(listenerA)
        bus.subscribe(listenerB)

        bus.publish(
            RuntimeRecoveryEvent.Started("before-unsubscribe")
        )

        bus.publish(
            RuntimeRecoveryEvent.Completed("after-unsubscribe")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-unsubscribe"),
                RuntimeRecoveryEvent.Completed("after-unsubscribe")
            ),
            eventsA
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-unsubscribe")
            ),
            eventsB
        )
    }
}
