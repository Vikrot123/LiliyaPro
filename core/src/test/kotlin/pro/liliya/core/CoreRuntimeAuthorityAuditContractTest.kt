package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeAuthorityAuditContractTest {

    @Test
    fun `runtime audit preserves authority metadata through pipeline`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "authority-test",
                    reason = "verify authority propagation",
                    authority = RuntimeActionAuthorityContext(
                        source = "authority-test",
                        level = RuntimeAuthorityLevel.USER
                    )
                )
            )

            assertTrue(result.success)

            val audit = CoreRuntime.getRuntimeActionAudit()

            assertTrue(audit.isNotEmpty())

            val record = audit.last()

            assertEquals(
                "authority-test",
                record.authoritySource
            )

            assertEquals(
                RuntimeAuthorityLevel.USER,
                record.authorityLevel
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
