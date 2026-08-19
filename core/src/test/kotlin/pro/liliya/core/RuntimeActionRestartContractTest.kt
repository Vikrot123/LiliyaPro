package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionRestartContractTest {

    @Test
    fun system_can_execute_runtime_restart_action() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val result = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.RESTART,
                    source = "restart-action-test",
                    reason = "verify restart action",
                    authority = RuntimeActionAuthorityContext(
                        source = "restart-action-test",
                        level = RuntimeAuthorityLevel.SYSTEM
                    )
                )
            )

        assertTrue(result.success)

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
