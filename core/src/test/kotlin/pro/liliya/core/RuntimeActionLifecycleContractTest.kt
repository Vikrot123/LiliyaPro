package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionLifecycleContractTest {

    @Test
    fun start_and_stop_commands_execute_through_action_pipeline() {

        val composition = DefaultRuntimeComposition()

        val authority = RuntimeActionAuthorityContext(
            source = "lifecycle-action-test",
            level = RuntimeAuthorityLevel.SYSTEM
        )

        val start = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.START,
                    source = "lifecycle-action-test",
                    reason = "verify start action",
                    authority = authority
                )
            )

        assertTrue(start.success)

        assertEquals(
            composition.runtimeState(),
            start.controlResult.currentState
        )

        val stop = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.STOP,
                    source = "lifecycle-action-test",
                    reason = "verify stop action",
                    authority = authority
                )
            )

        assertTrue(stop.success)

        assertEquals(
            composition.runtimeState(),
            stop.controlResult.currentState
        )

        assertEquals(
            2,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )
    }
}
