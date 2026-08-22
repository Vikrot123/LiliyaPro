package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryEventBusReentrantPublishIsolationContractTest {

    @Test
    fun reentrant_publish_does_not_break_current_delivery_order() {
        val bus = RuntimeRecoveryEventBus()

        val events = mutableListOf<String>()

        val first: (RuntimeRecoveryEvent) -> Unit = {
            events.add("first:${it::class.simpleName}")

            if (it is RuntimeRecoveryEvent.Started) {
                bus.publish(
                    RuntimeRecoveryEvent.Completed("nested")
                )
            }
        }

        val second: (RuntimeRecoveryEvent) -> Unit = {
            events.add("second:${it::class.simpleName}")
        }

        bus.subscribe(first)
        bus.subscribe(second)

        bus.publish(
            RuntimeRecoveryEvent.Started("root")
        )

        assertEquals(
            listOf(
                "first:Started",
                "first:Completed",
                "second:Completed",
                "second:Started"
            ),
            events
        )
    }
}
