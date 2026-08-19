package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeDispatcherFailureContractTest {

    @Test
    fun `runtime dispatcher returns failure when no handler exists`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.START,
                    source = "failure-test",
                    reason = "verify missing handler handling",
                      authority = RuntimeActionAuthorityContext(
                          source = "failure-test",
                          level = RuntimeAuthorityLevel.USER
                      )
                )
            )

            assertFalse(result.success)
            assertFalse(result.controlResult.success)

            assertEquals(
                RuntimeCommand.START,
                result.controlResult.command
            )

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.getRuntimeState()
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
