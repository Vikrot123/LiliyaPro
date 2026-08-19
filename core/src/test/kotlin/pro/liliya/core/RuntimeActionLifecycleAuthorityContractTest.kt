package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionLifecycleAuthorityContractTest {

    @Test
    fun user_cannot_execute_runtime_lifecycle_actions() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.START,
                    source = "authority-test-user",
                    reason = "verify lifecycle protection",
                    authority = RuntimeActionAuthorityContext(
                        source = "authority-test-user",
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

        assertEquals(
            CoreRuntimeState.STOPPED,
            composition.runtimeState()
        )
    }

    @Test
    fun system_can_execute_runtime_lifecycle_actions() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.START,
                    source = "authority-test-system",
                    reason = "verify lifecycle permission",
                    authority = RuntimeActionAuthorityContext(
                        source = "authority-test-system",
                        level = RuntimeAuthorityLevel.SYSTEM
                    )
                )
            )

        assertTrue(result.success)

        assertEquals(
            composition.runtimeState(),
            result.controlResult.currentState
        )

        composition.stopRuntime()
    }
}
