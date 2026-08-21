package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

class RuntimeEventBusDuplicateSubscriptionContractTest {

    @Test
    fun duplicate_subscription_must_not_deliver_event_twice() {
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeEvent>()

        val listener: (RuntimeEvent) -> Unit = {
            events.add(it)
        }

        RuntimeEventBus.subscribe(listener)
        RuntimeEventBus.subscribe(listener)

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        assertEquals(
            1,
            events.size
        )

        RuntimeEventBus.unsubscribe(listener)
        RuntimeEventBus.clear()
    }
}
