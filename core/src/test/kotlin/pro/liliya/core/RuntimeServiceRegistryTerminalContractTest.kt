package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.*

class RuntimeServiceRegistryTerminalContractTest {

    @Test
    fun runtimeServiceRegistryCannotRestartAfterStop() {

        val registry = RuntimeServiceRegistry()

        registry.register(TestService())

        registry.startAll()

        registry.stopAll()

        assertThrows(IllegalStateException::class.java) {
            registry.startAll()
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
