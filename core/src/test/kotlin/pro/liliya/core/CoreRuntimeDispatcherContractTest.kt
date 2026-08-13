package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeDispatcherContractTest {

    @Test
    fun `runtime dispatcher executes health action through pipeline`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "dispatcher-test",
                    reason = "verify dispatcher pipeline"
                )
            )

            assertTrue(result.success)
            assertTrue(result.controlResult.success)

            val history = CoreRuntime.getRuntimeCommandHistory()

            assertTrue(history.isNotEmpty())
            assertTrue(history.last().success)

        } finally {
            CoreRuntime.stop()
        }
    }
}
