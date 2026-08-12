package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState

class CoreRuntimeRuntimeServiceLifecycleContractTest {

    @Test
    fun runtimeServicesFollowCoreRuntimeLifecycle() {
        val service = TestRuntimeService()

        CoreRuntime.stop()

        CoreRuntime.registerRuntimeService(service)

        CoreRuntime.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        CoreRuntime.stop()

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )
    }

    private class TestRuntimeService : RuntimeService {

        override val name = "TEST_CORE_RUNTIME_SERVICE"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
