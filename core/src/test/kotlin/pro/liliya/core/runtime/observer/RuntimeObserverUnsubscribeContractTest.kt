package pro.liliya.core.runtime.observer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.RuntimeEvent

class RuntimeObserverUnsubscribeContractTest {

    @Test
    fun `observer does not receive event after unsubscribe`() {

        val registry =
            DefaultRuntimeObserverRegistry()

        var count = 0

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                count++
            }
        }

        registry.subscribe(observer)

        registry.unsubscribe(observer)

        registry.publish(RuntimeEvent.SystemStart)

        assertEquals(0, count)
    }
}
