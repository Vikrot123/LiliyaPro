package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeActionAuditContractTest {

    @Test
    fun `runtime action audit records successful action execution`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "audit-test",
                    reason = "verify audit trail",
authority = RuntimeActionAuthorityContext(
    source = "audit-test",
    level = RuntimeAuthorityLevel.USER
)
                )
            )

            assertTrue(result.success)

            val audit = CoreRuntime.getRuntimeActionAudit()

            assertTrue(audit.isNotEmpty())
            assertTrue(audit.last().success)
            assertTrue(
                audit.last().message.isNotEmpty()
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
