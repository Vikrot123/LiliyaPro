package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreDiagnosticEventBusContractTest {

    @Test
    fun eventBusDeliversDiagnosticEventToListener() {

        val bus = CoreDiagnosticEventBus()

        var received: CoreDiagnosticEvent? = null

        val listener = object : CoreDiagnosticEventListener {

            override fun onDiagnosticEvent(
                event: CoreDiagnosticEvent
            ) {
                received = event
            }
        }

        bus.register(listener)

        val snapshot = CoreRuntime.snapshot()

        val event = CoreDiagnosticEvent(
            type = CoreDiagnosticEventType.RUNTIME_STARTED,
            snapshot = snapshot
        )

        bus.publish(event)

        assertEquals(
            CoreDiagnosticEventType.RUNTIME_STARTED,
            received?.type
        )

        assertEquals(
            snapshot,
            received?.snapshot
        )
    }
}
