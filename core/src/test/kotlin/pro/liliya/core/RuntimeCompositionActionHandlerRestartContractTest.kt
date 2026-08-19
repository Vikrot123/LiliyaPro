package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCompositionActionHandlerRestartContractTest {

    @Test
    fun action_handler_pipeline_is_restored_after_runtime_restart() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        val first = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "restart-test",
                    authority = RuntimeActionAuthorityContext(
                        source = "restart-test",
                        level = RuntimeAuthorityLevel.USER
                    )
                )
            )

        assertTrue(first.success)

        composition.stopRuntime()
        composition.prepareRuntime()
        composition.startRuntime()

        val second = composition
            .actionDispatcher()
            .dispatch(
                RuntimeActionRequest(
                    command = RuntimeCommand.HEALTH_CHECK,
                    source = "restart-test",
                    authority = RuntimeActionAuthorityContext(
                        source = "restart-test",
                        level = RuntimeAuthorityLevel.USER
                    )
                )
            )

        assertTrue(second.success)

        composition.stopRuntime()
    }
}
