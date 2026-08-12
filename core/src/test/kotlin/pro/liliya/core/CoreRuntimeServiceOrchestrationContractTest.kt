package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.*

class CoreRuntimeServiceOrchestrationContractTest {

    @Test
    fun runtimeServicesFollowCoreRuntimeLifecycle() {

        val registry = RuntimeServiceRegistry()

        val service = TestRuntimeService()

        registry.register(service)

        registry.startAll()

        assertEquals(
            RuntimeServiceState.RUNNING,
            registry.getStates()["TEST_RUNTIME_SERVICE"]
        )

        registry.stopAll()

        assertEquals(
            RuntimeServiceState.STOPPED,
            registry.getStates()["TEST_RUNTIME_SERVICE"]
        )
    }


    private class TestRuntimeService : RuntimeService {

        override val name = "TEST_RUNTIME_SERVICE"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
