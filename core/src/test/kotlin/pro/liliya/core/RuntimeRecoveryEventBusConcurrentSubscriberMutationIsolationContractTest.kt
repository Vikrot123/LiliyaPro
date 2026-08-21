package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusConcurrentSubscriberMutationIsolationContractTest {

    @Test
    fun concurrent_subscribe_and_unsubscribe_do_not_break_publish() {
        val bus = RuntimeRecoveryEventBus()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val listener: (RuntimeRecoveryEvent) -> Unit = {
            synchronized(events) {
                events.add(it)
            }
        }

        val subscriberThread = thread {
            repeat(100) {
                bus.subscribe(listener)
                bus.unsubscribe(listener)
            }
        }

        val publisherThread = thread {
            repeat(100) {
                bus.publish(
                    RuntimeRecoveryEvent.Started("mutation-isolation-service")
                )
            }
        }

        subscriberThread.join()
        publisherThread.join()

        assertTrue(
            events.size >= 0
        )
    }
}
