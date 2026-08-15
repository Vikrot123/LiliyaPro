package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeControlContractTest {

    @Test
    fun `health check command returns runtime availability`() {

        CoreRuntime.start()

        try {
            val control = CoreRuntime.runtimeControl()

            val result = control.execute(
                RuntimeCommand.HEALTH_CHECK
            )

            assertTrue(result.success)

            assertEquals(
                CoreRuntimeState.RUNNING,
                result.currentState
            )

            assertTrue(
                result.status.available
            )

            assertTrue(
                result.status.report.recovery.recovered
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
