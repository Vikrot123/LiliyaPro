package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeHealthContractTest {

    @Test
    fun `runtime health snapshot reflects running state`() {

        CoreRuntime.start()

        try {
            val snapshot =
                CoreRuntime.getRuntimeHealthSnapshot()

            assertEquals(
                CoreRuntimeState.RUNNING,
                snapshot.state
            )

            assertNotNull(snapshot.startedAt)

            assertNotNull(snapshot.readyAt)

            assertNotNull(snapshot.uptimeMillis)

            assertTrue(
                snapshot.eventCount > 0
            )

            assertEquals(
                RuntimeEvent.RuntimeReady,
                snapshot.lastEvent
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
