package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusPublishSnapshotIsolationContractTest {

    @Test
    fun publish_uses_snapshot_and_does_not_deliver_current_event_to_new_subscriber() {
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
            RuntimeRecoveryEvent.Started("snapshot-first")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("snapshot-first")
            ),
            eventsA
        )

        assertEquals(
            emptyList(),
            eventsB
        )

        bus.publish(
            RuntimeRecoveryEvent.Completed("snapshot-second")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Completed("snapshot-second")
            ),
            eventsB
        )
    }
}
