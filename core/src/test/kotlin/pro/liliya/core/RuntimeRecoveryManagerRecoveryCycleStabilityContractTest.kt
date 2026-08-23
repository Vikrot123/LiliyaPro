package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerRecoveryCycleStabilityContractTest {

    @Test
    fun repeated_failures_recover_without_poisoning_state() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val service = object : RuntimeService {
            override val name = "cycle-service"

            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        }

        registry.register(service)

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry
        )

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "cycle-service",
                reason = "first-failure",
                sourceRegistry = registry
            )
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "cycle-service",
                reason = "second-failure",
                sourceRegistry = registry
            )
        )

        assertEquals(
            2,
            supervisor.getRestartCount("cycle-service")
        )

        val snapshot = manager.snapshot()

        assertEquals(
            "cycle-service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )

        assertEquals(
            RuntimeServiceState.RUNNING,
            service.state
        )

        RuntimeEventBus.clear()
    }
}
