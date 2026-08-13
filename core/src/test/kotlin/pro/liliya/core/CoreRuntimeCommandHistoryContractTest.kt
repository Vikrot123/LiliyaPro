package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeCommandHistoryContractTest {

    @Test
    fun `runtime command history records executed commands`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.executeRuntimeCommand(
                RuntimeCommand.HEALTH_CHECK
            )

            assertTrue(result.success)

            val history = CoreRuntime.getRuntimeCommandHistory()

            assertFalse(history.isEmpty())

            val last = history.last()

            assertEquals(
                RuntimeCommand.HEALTH_CHECK,
                last.command
            )

            assertTrue(last.success)

        } finally {
            CoreRuntime.stop()
        }
    }
}
