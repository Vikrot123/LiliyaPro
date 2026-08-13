package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeHealthReportContractTest {

    @Test
    fun `runtime health report contains complete runtime diagnostics`() {

        CoreRuntime.start()

        try {
            val report =
                CoreRuntime.getRuntimeHealthReport()

            assertEquals(
                CoreRuntimeState.RUNNING,
                report.runtime.state
            )

            assertFalse(
                report.failure.failed
            )

            assertTrue(
                report.recovery.recovered
            )

            assertTrue(
                report.runtime.eventCount > 0
            )

            assertEquals(
                RuntimeEvent.RuntimeReady,
                report.runtime.lastEvent
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
