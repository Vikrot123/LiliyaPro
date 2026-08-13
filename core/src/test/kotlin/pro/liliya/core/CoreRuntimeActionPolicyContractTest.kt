package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeActionPolicyContractTest {

    @Test
    fun `runtime policy allows health check action`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "policy-test",
                    reason = "verify allowed action",
authority = RuntimeActionAuthorityContext(
    source = "policy-test",
    level = RuntimeAuthorityLevel.USER
)
                )
            )

            assertTrue(result.success)

            val audit = CoreRuntime.getRuntimeActionAudit()

            assertTrue(audit.isNotEmpty())
            assertTrue(audit.last().success)

        } finally {
            CoreRuntime.stop()
        }
    }


    @Test
    fun `runtime policy denies unsupported action`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.START,
                    source = "policy-test",
                    reason = "verify denied action"
                )
            )

            assertFalse(result.success)
            assertFalse(result.controlResult.success)

            val audit = CoreRuntime.getRuntimeActionAudit()

            assertTrue(audit.isNotEmpty())
            assertFalse(audit.last().success)

        } finally {
            CoreRuntime.stop()
        }
    }
}
