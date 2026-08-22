package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusSelfUnsubscribeDuringPublishIsolationContractTest {

    @Test
    fun subscriber_can_remove_itself_without_breaking_current_delivery() {
        val bus = RuntimeRecoveryEventBus()

        val events = mutableListOf<String>()

        lateinit var selfRemoving: (RuntimeRecoveryEvent) -> Unit

        selfRemoving = {
            events.add("self:${it::class.simpleName}")
            bus.unsubscribe(selfRemoving)
        }

        val stable: (RuntimeRecoveryEvent) -> Unit = {
            events.add("stable:${it::class.simpleName}")
        }

        bus.subscribe(selfRemoving)
        bus.subscribe(stable)

        bus.publish(
            RuntimeRecoveryEvent.Started("first")
        )

        bus.publish(
            RuntimeRecoveryEvent.Completed("second")
        )

        assertEquals(
            listOf(
                "self:Started",
                "stable:Started",
                "stable:Completed"
            ),
            events
        )
    }
}
