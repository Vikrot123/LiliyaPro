package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionAuditRestartIsolationContractTest {

    @Test
    fun action_audit_state_isolated_after_runtime_restart() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.HEALTH_CHECK,
            source = "audit-restart-test",
            reason = "verify audit isolation",
            authority = RuntimeActionAuthorityContext(
                source = "audit-restart-test",
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

        composition.stopRuntime()
        composition.prepareRuntime()
        composition.startRuntime()

        assertEquals(
            0,
            composition.actionAuditProvider()
                .snapshot()
                .size
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

        composition.stopRuntime()
    }
}
