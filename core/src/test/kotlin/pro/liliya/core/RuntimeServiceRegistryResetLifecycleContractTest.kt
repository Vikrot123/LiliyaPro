package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.*

class RuntimeServiceRegistryResetLifecycleContractTest {

    @Test
    fun runtimeServiceRegistryReset_allowsFreshLifecycleAfterStop() {
        val registry = RuntimeServiceRegistry()

        val first = TestService("RESET_SERVICE")

        registry.register(first)
        registry.startAll()
        registry.stopAll()

        registry.reset()

        val second = TestService("RESET_SERVICE")

        registry.register(second)
        registry.startAll()

        assertEquals(
            RuntimeServiceState.RUNNING,
            second.state
        )

        assertEquals(
            emptyList<RuntimeServiceFailure>(),
            registry.getFailures()
        )

        registry.stopAll()
    }

    private class TestService(
        override val name: String
    ) : RuntimeService {

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
