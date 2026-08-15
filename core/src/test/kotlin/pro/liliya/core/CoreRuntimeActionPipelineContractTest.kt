package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeActionPipelineContractTest {

    @Test
    fun `runtime action pipeline executes command through executor`() {

        CoreRuntime.start()

        try {
            val request = RuntimeActionRequest(
                command = RuntimeCommand.HEALTH_CHECK,
                source = "contract-test",
                reason = "verify action pipeline",
                authority = RuntimeActionAuthorityContext(
                    source = "contract-test",
                    level = RuntimeAuthorityLevel.USER
                )
            )

            val result = CoreRuntime.dispatchRuntimeAction(request)

            println("ACTION SUCCESS=${result.success}")
            println("CONTROL SUCCESS=${result.controlResult.success}")
            println("CONTROL MESSAGE=${result.controlResult.message}")

            val history = CoreRuntime.getRuntimeCommandHistory()

            println("HISTORY SIZE=${history.size}")
            println("HISTORY=$history")

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
