package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusClearLifecycleIsolationContractTest {

    @Test
    fun clear_removes_old_subscribers_and_allows_clean_reuse() {
        val bus = RuntimeRecoveryEventBus()

        val firstEvents = mutableListOf<RuntimeRecoveryEvent>()
        val secondEvents = mutableListOf<RuntimeRecoveryEvent>()

        val firstListener: (RuntimeRecoveryEvent) -> Unit = {
            firstEvents.add(it)
        }

        val secondListener: (RuntimeRecoveryEvent) -> Unit = {
            secondEvents.add(it)
        }

        bus.subscribe(firstListener)

        bus.publish(
            RuntimeRecoveryEvent.Started("before-clear")
        )

        bus.clear()

        bus.publish(
            RuntimeRecoveryEvent.Started("after-clear-without-listener")
        )

        bus.subscribe(secondListener)

        bus.publish(
            RuntimeRecoveryEvent.Completed("after-reuse")
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Started("before-clear")
            ),
            firstEvents
        )

        assertEquals(
            listOf<RuntimeRecoveryEvent>(
                RuntimeRecoveryEvent.Completed("after-reuse")
            ),
            secondEvents
        )
    }
}
