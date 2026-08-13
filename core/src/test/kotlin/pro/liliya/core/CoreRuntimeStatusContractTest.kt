package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeStatusContractTest {

    @Test
    fun `runtime status exposes unified runtime availability`() {

        CoreRuntime.start()

        try {
            val status =
                CoreRuntime.getRuntimeStatusSnapshot()

            assertTrue(status.available)

            assertEquals(
                CoreRuntimeState.RUNNING,
                status.report.runtime.state
            )

            assertEquals(
                RuntimeEvent.RuntimeReady,
                status.report.runtime.lastEvent
            )

            assertTrue(
                status.report.runtime.eventCount > 0
            )

            assertTrue(
                status.report.recovery.recovered
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
