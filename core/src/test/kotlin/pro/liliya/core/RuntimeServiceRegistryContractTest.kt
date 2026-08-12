package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.*

class RuntimeServiceRegistryContractTest {

    @Test
    fun runtimeServiceRegistryControlsLifecycle() {

        val registry = RuntimeServiceRegistry()

        val service = TestService()

        registry.register(service)

        registry.startAll()

        assertEquals(
            RuntimeServiceState.RUNNING,
            registry.getStates()["TEST_SERVICE"]
        )

        registry.stopAll()

        assertEquals(
            RuntimeServiceState.STOPPED,
            registry.getStates()["TEST_SERVICE"]
        )
    }


    @Test
    fun runtimeServiceRegistryRejectsDuplicateServices() {

        val registry = RuntimeServiceRegistry()

        registry.register(TestService())

        assertThrows(IllegalStateException::class.java) {

            registry.register(TestService())
        }
    }


    private class TestService : RuntimeService {

        override val name = "TEST_SERVICE"

        override var state = RuntimeServiceState.CREATED


        override fun start() {

            state = RuntimeServiceState.RUNNING
        }


        override fun stop() {

            state = RuntimeServiceState.STOPPED
        }
    }
}
