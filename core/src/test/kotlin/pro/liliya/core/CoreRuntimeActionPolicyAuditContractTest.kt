package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.policy.RuntimeActionPolicyDecision

class CoreRuntimeActionPolicyAuditContractTest {

    @Test
    fun `runtime action audit contains policy decision metadata`() {

        CoreRuntime.start()

        try {
            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "policy-audit-test",
                    reason = "verify policy metadata",
                    authority = RuntimeActionAuthorityContext(
                        source = "policy-audit-test",
                        level = RuntimeAuthorityLevel.USER
                    )
                )
            )

            assertTrue(result.success)

            val audit = CoreRuntime.getRuntimeActionAudit()

            assertTrue(audit.isNotEmpty())

            val record = audit.last()

            assertEquals(
                "health-check-authority",
                record.policyId
            )

            assertEquals(
                RuntimeActionPolicyDecision.ALLOW,
                record.policyDecision
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
