package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionDispatcherMultiCycleIsolationContractTest {

    @Test
    fun dispatcher_state_isolated_across_multiple_runtime_cycles() {

        val composition = DefaultRuntimeComposition()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.HEALTH_CHECK,
            source = "multi-cycle-test",
            reason = "verify repeated runtime cycles",
            authority = RuntimeActionAuthorityContext(
                source = "multi-cycle-test",
                level = RuntimeAuthorityLevel.USER
            )
        )

        repeat(3) {

            composition.startRuntime()

            val result = composition
                .actionDispatcher()
                .dispatch(request)

            assertTrue(result.success)

            assertEquals(
                1,
                composition.actionAuditProvider()
                    .snapshot()
                    .size
            )

            assertEquals(
                1,
                composition.commandHistoryProvider()
                    .snapshot()
                    .size
            )

            composition.stopRuntime()
            composition.prepareRuntime()

            assertEquals(
                0,
                composition.actionAuditProvider()
                    .snapshot()
                    .size
            )

            assertEquals(
                0,
                composition.commandHistoryProvider()
                    .snapshot()
                    .size
            )
        }
    }
}
