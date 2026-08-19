package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCompositionControlStatePrepareIsolationContractTest {

    @Test
    fun control_state_is_cleared_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.registerRuntimeControls()
        composition.registerRuntimeActionHandlers()

        val beforeHistory =
            composition.commandHistoryProvider().snapshot()

        val beforeAudit =
            composition.actionAuditProvider().snapshot()

        assertTrue(beforeHistory.isEmpty())
        assertTrue(beforeAudit.isEmpty())

        composition.prepareRuntime()

        val afterHistory =
            composition.commandHistoryProvider().snapshot()

        val afterAudit =
            composition.actionAuditProvider().snapshot()

        assertTrue(afterHistory.isEmpty())
        assertTrue(afterAudit.isEmpty())
    }

    @Test
    fun action_handlers_are_cleared_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.registerRuntimeControls()
        composition.registerRuntimeActionHandlers()

        composition.prepareRuntime()

        composition.registerRuntimeControls()

        val request =
            RuntimeActionRequest(
                command = RuntimeCommand.HEALTH_CHECK
            )

        val result =
            composition.actionDispatcher()
                .dispatch(request)

        assertFalse(result.success)
    }
}
