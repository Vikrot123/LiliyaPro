package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeActionDispatcherHandlerExceptionContractTest {

    @Test
    fun missing_handler_creates_failure_audit_without_runtime_crash() {

        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.START,
            source = "handler-failure-test",
            reason = "verify dispatcher isolation",
            authority = RuntimeActionAuthorityContext(
                source = "handler-failure-test",
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
              1,
              composition.commandHistoryProvider()
                  .snapshot()
                  .size
          )

          composition.stopRuntime()
    }
}
