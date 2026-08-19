package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.action.RuntimeActionRequest

class CoreRuntimeControlContractTest {

    @Test
    fun `health check command returns runtime availability`() {

        CoreRuntime.start()

        try {
            val control = CoreRuntime.runtimeControl()

              val result = control.execute(
                  RuntimeActionRequest(
                      command = RuntimeCommand.HEALTH_CHECK,
                      source = "core-runtime-control-contract-test"
                  )
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
