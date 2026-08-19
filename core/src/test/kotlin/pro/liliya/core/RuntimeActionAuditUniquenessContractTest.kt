package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionAuditUniquenessContractTest {

    @Test
    fun one_dispatch_creates_one_audit_record() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.HEALTH_CHECK,
            source = "audit-test",
            reason = "verify audit uniqueness",
            authority = RuntimeActionAuthorityContext(
                source = "audit-test",
                level = RuntimeAuthorityLevel.USER
            )
        )

        composition
            .actionDispatcher()
            .dispatch(request)

        assertEquals(
            1,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        composition
            .actionDispatcher()
            .dispatch(request)

        assertEquals(
            2,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        composition.stopRuntime()
    }
}
