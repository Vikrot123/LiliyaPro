package pro.liliya.core.runtime.observer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.RuntimeEvent

class RuntimeObserverContractTest {

    @Test
    fun `observer receives published runtime event`() {

        val registry =
            DefaultRuntimeObserverRegistry()

        var received: RuntimeEvent? = null

        val observer = object : RuntimeObserver {
            override fun onRuntimeEvent(event: RuntimeEvent) {
                received = event
            }
        }

        registry.subscribe(observer)

        val event = RuntimeEvent.SystemStart

        registry.publish(event)

        assertEquals(event, received)
    }
}
