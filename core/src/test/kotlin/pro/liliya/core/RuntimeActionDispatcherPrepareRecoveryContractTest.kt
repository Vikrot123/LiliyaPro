package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionDispatcherPrepareRecoveryContractTest {

    @Test
    fun dispatcher_pipeline_recovers_after_prepare_runtime() {

        val composition = DefaultRuntimeComposition()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.HEALTH_CHECK,
            source = "dispatcher-recovery-test",
            reason = "verify dispatcher recovery",
            authority = RuntimeActionAuthorityContext(
                source = "dispatcher-recovery-test",
                level = RuntimeAuthorityLevel.USER
            )
        )

        composition.startRuntime()

        val first = composition
            .actionDispatcher()
            .dispatch(request)

        assertTrue(first.success)

        composition.stopRuntime()
        composition.prepareRuntime()
        composition.startRuntime()

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

        assertEquals(
            1,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        composition.stopRuntime()
    }
}
