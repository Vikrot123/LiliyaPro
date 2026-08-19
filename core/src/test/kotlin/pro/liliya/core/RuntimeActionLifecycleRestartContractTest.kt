package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionLifecycleRestartContractTest {

    @Test
    fun complete_action_lifecycle_is_restored_after_restart() {
        val composition = DefaultRuntimeComposition()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.HEALTH_CHECK,
            source = "lifecycle-restart-test",
            reason = "verify complete action lifecycle",
            authority = RuntimeActionAuthorityContext(
                source = "lifecycle-restart-test",
                level = RuntimeAuthorityLevel.USER
            )
        )

        composition.startRuntime()

        val first = composition
            .actionDispatcher()
            .dispatch(request)

        assertTrue(first.success)
        assertTrue(first.controlResult.success)

        assertEquals(
            1,
            composition.commandHistoryProvider()
                .snapshot()
                .size
        )

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
            composition.commandHistoryProvider()
                .snapshot()
                .size
        )

        assertEquals(
            0,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        val second = composition
            .actionDispatcher()
            .dispatch(request)

        assertTrue(second.success)
        assertTrue(second.controlResult.success)

        assertEquals(
            1,
            composition.commandHistoryProvider()
                .snapshot()
                .size
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
