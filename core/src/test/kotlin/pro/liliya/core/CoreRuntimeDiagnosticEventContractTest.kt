package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticEventContractTest {

    @Test
    fun runtimeStartPublishesStartedDiagnosticEvent() {
        CoreRuntime.stop()

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

        CoreRuntime.start()

        val event = CoreDiagnosticEvent(
            type = CoreDiagnosticEventType.RUNTIME_STARTED,
            snapshot = CoreRuntime.snapshot()
        )

        bus.publish(event)

        assertEquals(
            CoreDiagnosticEventType.RUNTIME_STARTED,
            received?.type
        )

        assertEquals(
            CoreRuntimeState.RUNNING,
            received?.snapshot?.runtimeState
        )

        CoreRuntime.stop()
    }
}
