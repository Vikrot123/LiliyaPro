package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeDiagnosticStatusContractTest {

    @Test
    fun `diagnostic snapshot exposes runtime status`() {

        CoreRuntime.start()

        try {
            val diagnostic =
                CoreRuntime.snapshot()

            val status =
                diagnostic.runtimeStatusSnapshot

            assertNotNull(status)

            assertTrue(
                status!!.available
            )

            assertTrue(
                status.report.recovery.recovered
            )

            assertTrue(
                status.report.runtime.eventCount > 0
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
