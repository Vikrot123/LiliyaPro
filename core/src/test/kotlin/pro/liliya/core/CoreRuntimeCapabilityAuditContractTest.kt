package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeCapabilityAuditContractTest {

    @Test
    fun `runtime audit preserves capability metadata`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "capability-audit-test",
                    reason = "verify capability metadata propagation",
                    authority = RuntimeActionAuthorityContext(
                        source = "capability-audit-test",
                        level = RuntimeAuthorityLevel.USER
                    )
                )
            )

            assertTrue(result.success)

            val audit = CoreRuntime.getRuntimeActionAudit()

            assertTrue(audit.isNotEmpty())

            val record = audit.last()

            assertEquals(
                true,
                record.capabilityAllowed
            )

            assertEquals(
                "User health capability",
                record.capabilityDescription
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
