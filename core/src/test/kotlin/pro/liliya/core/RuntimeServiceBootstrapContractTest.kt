package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapContractTest {

    @Test
    fun bootstrapStartsServicesFromProvider() {

        val service = TestRuntimeService()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(service)
            }
        }

        val registry = RuntimeServiceRegistry()

        val bootstrap = RuntimeServiceBootstrap(
            provider,
            registry
        )

        bootstrap.start()

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        bootstrap.stop()

        assertEquals(
            RuntimeServiceState.STOPPED,
            service.state
        )
    }



    @Test
    fun bootstrapStop_uninstallsRecoveryManagerListener() {

        RuntimeEventBus.clear()

        val service = TestRuntimeService()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(service)
            }
        }

        val registry = RuntimeServiceRegistry()

        val bootstrap = RuntimeServiceBootstrap(
            provider,
            registry
        )

        bootstrap.start()

        bootstrap.stop()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "after shutdown"
            )
        )

        assertEquals(
            0,
            bootstrap.getRestartCount(service.name)
        )

        RuntimeEventBus.clear()
    }



    @Test
    fun bootstrap_restartLifecycle_restoresSingleRecoveryListener() {
        RuntimeEventBus.clear()

        val service = TestRuntimeService()

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(service)
            }
        }

        val registry = RuntimeServiceRegistry()

        val bootstrap = RuntimeServiceBootstrap(
            provider,
            registry
        )

        bootstrap.start()
        bootstrap.stop()

        bootstrap.start()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = service.name,
                reason = "restart lifecycle"
            )
        )

        assertEquals(
            1,
            bootstrap.getRestartCount(service.name)
        )

        bootstrap.stop()

        RuntimeEventBus.clear()
    }

    private class TestRuntimeService : RuntimeService {

        override val name = "BOOTSTRAP_TEST_SERVICE"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
