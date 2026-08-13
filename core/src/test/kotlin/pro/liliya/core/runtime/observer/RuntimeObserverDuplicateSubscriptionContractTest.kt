package pro.liliya.core.runtime.observer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.RuntimeEvent

class RuntimeObserverDuplicateSubscriptionContractTest {

    @Test
    fun `observer receives event only once after duplicate subscription`() {

        val registry =
            DefaultRuntimeObserverRegistry()

        var count = 0

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                count++
            }
        }

        registry.subscribe(observer)
        registry.subscribe(observer)

        registry.publish(RuntimeEvent.SystemStart)

        assertEquals(1, count)
    }
}
