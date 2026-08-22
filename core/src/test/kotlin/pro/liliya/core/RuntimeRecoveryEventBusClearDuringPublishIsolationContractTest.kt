package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusClearDuringPublishIsolationContractTest {

    @Test
    fun clear_during_publish_does_not_break_current_snapshot_delivery() {
        val bus = RuntimeRecoveryEventBus()

        val eventsA = mutableListOf<RuntimeRecoveryEvent>()
        val eventsB = mutableListOf<RuntimeRecoveryEvent>()

        val listenerA: (RuntimeRecoveryEvent) -> Unit = {
            eventsA.add(it)
            bus.clear()
        }

        val listenerB: (RuntimeRecoveryEvent) -> Unit = {
            eventsB.add(it)
        }

        bus.subscribe(listenerA)
        bus.subscribe(listenerB)

        bus.publish(
            RuntimeRecoveryEvent.Started("before-clear")
        )

        bus.publish(
            RuntimeRecoveryEvent.Completed("after-clear")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-clear")
            ),
            eventsA
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-clear")
            ),
            eventsB
        )
    }
}
