package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeControlApiContractTest {

    @Test
    fun `core runtime exposes runtime health command through public api`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.executeRuntimeCommand(
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
