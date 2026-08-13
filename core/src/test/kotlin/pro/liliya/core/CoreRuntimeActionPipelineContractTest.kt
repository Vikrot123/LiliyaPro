package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionExecutor
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeActionPipelineContractTest {

    @Test
    fun `runtime action pipeline executes command through executor`() {

        CoreRuntime.start()

        try {
            val request = RuntimeActionRequest(
                command = RuntimeCommand.HEALTH_CHECK,
                source = "contract-test",
                reason = "verify action pipeline"
            )

            val executor = RuntimeActionExecutor()

            val result = executor.execute(request)

            assertTrue(result.success)

            assertTrue(
                result.controlResult.success
            )

            val history = CoreRuntime.getRuntimeCommandHistory()

            assertTrue(
                history.isNotEmpty()
            )

            assertTrue(
                history.last().success
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
