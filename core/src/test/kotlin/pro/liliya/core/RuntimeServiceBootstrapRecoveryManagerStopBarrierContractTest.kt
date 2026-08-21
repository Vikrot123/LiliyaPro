package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeServiceBootstrapRecoveryManagerStopBarrierContractTest {

    @Test
    fun stopped_bootstrap_recovery_manager_does_not_receive_runtime_failures() {

        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val service = object : RuntimeService {
            override val name = "stop-barrier-service"

            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        }

        val provider = object : RuntimeServiceProvider {
            override fun provideServices(): List<RuntimeService> {
                return listOf(service)
            }
        }

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry
        )

        val bootstrap = RuntimeServiceBootstrap(
            provider,
            registry,
            supervisor,
            manager
        )

        bootstrap.start()

        bootstrap.stop()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "stop-barrier-service",
                reason = "after-stop"
            )
        )

        assertEquals(
            0,
            supervisor.getRestartCount("stop-barrier-service")
        )

        RuntimeEventBus.clear()
    }
}
