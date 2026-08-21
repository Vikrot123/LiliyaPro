package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusConcurrentPublishIsolationContractTest {

    @Test
    fun concurrent_publish_does_not_lose_recovery_events() {
        val bus = RuntimeRecoveryEventBus()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        bus.subscribe {
            synchronized(events) {
                events.add(it)
            }
        }

        val first = thread {
            bus.publish(
                RuntimeRecoveryEvent.Started("concurrent-a")
            )
        }

        val second = thread {
            bus.publish(
                RuntimeRecoveryEvent.Completed("concurrent-b")
            )
        }

        first.join()
        second.join()

        assertEquals(
            2,
            events.size
        )
    }
}
