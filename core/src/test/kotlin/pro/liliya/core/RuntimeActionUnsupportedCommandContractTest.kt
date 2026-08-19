package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionUnsupportedCommandContractTest {

    @Test
    fun unsupported_action_command_creates_failure_audit_without_crash() {

        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.START,
            source = "unsupported-command-test",
            reason = "verify unsupported action isolation",
            authority = RuntimeActionAuthorityContext(
                source = "unsupported-command-test",
                level = RuntimeAuthorityLevel.USER
            )
        )

        val result = composition
            .actionDispatcher()
            .dispatch(request)

        assertFalse(result.success)

        assertEquals(
            1,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        assertEquals(
            composition.runtimeState(),
            result.controlResult.currentState
        )

        composition.stopRuntime()
    }
}
