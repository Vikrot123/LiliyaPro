package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeTelemetryRestartContractTest {

    @Test
    fun `runtime telemetry resets on restart`() {

        CoreRuntime.start()

        val first = CoreRuntime.getRuntimeTelemetrySnapshot()

        CoreRuntime.stop()

        CoreRuntime.start()

        try {
            val second = CoreRuntime.getRuntimeTelemetrySnapshot()

            assertNotNull(first.startedAt)
            assertNotNull(second.startedAt)

            assertTrue(
                second.eventCount > 0
            )

            assertEquals(
                RuntimeEvent.RuntimeReady,
                second.lastEvent
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
