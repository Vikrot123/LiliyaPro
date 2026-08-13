package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeTelemetryContractTest {

    @Test
    fun `runtime telemetry captures lifecycle timeline`() {

        CoreRuntime.start()

        try {
            val snapshot = CoreRuntime.getRuntimeTelemetrySnapshot()

            assertNotNull(snapshot.startedAt)

            assertNotNull(snapshot.readyAt)

            assertTrue(
                snapshot.eventCount > 0
            )

            assertTrue(
                snapshot.lastEvent == RuntimeEvent.RuntimeReady
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
