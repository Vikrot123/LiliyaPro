package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionControlStateConsistencyContractTest {

    @Test
    fun action_result_state_matches_runtime_state() {

        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.HEALTH_CHECK,
            source = "control-state-test",
            reason = "verify action state consistency",
            authority = RuntimeActionAuthorityContext(
                source = "control-state-test",
                level = RuntimeAuthorityLevel.USER
            )
        )

        val result = composition
            .actionDispatcher()
            .dispatch(request)

        assertTrue(result.success)

        assertEquals(
            composition.runtimeState(),
            result.controlResult.currentState
        )

        composition.stopRuntime()
    }
}
