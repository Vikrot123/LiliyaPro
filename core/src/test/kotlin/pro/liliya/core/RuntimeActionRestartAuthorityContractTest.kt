package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionRestartAuthorityContractTest {

    @Test
    fun user_cannot_execute_runtime_restart_action() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val result = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.RESTART,
                    source = "restart-authority-user-test",
                    reason = "verify restart protection",
                    authority = RuntimeActionAuthorityContext(
                        source = "restart-authority-user-test",
                        level = RuntimeAuthorityLevel.USER
                    )
                )
            )

        assertFalse(result.success)

        assertEquals(
            1,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        composition.stopRuntime()
    }


    @Test
    fun system_can_execute_runtime_restart_action_after_authority_check() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val result = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.RESTART,
                    source = "restart-authority-system-test",
                    reason = "verify restart permission",
                    authority = RuntimeActionAuthorityContext(
                        source = "restart-authority-system-test",
                        level = RuntimeAuthorityLevel.SYSTEM
                    )
                )
            )

        assertEquals(true, result.success)

        assertEquals(
            composition.runtimeState(),
            result.controlResult.currentState
        )

        assertEquals(
            1,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        composition.stopRuntime()
    }

}

