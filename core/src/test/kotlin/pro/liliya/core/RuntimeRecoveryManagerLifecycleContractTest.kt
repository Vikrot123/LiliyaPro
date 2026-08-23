package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerLifecycleContractTest {

    @Test
    fun uninstall_stops_recovery_until_reinstall() {

        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "lifecycle-service"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

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
                serviceName = "lifecycle-service",
                reason = "first",
                sourceRegistry = registry
            )
        )

        assertEquals(
            1,
            supervisor.getRestartCount("lifecycle-service")
        )

        manager.uninstall()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "lifecycle-service",
                reason = "second",
                sourceRegistry = registry
            )
        )

        assertEquals(
            1,
            supervisor.getRestartCount("lifecycle-service")
        )

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "lifecycle-service",
                reason = "third",
                sourceRegistry = registry
            )
        )

        assertEquals(
            2,
            supervisor.getRestartCount("lifecycle-service")
        )

        RuntimeEventBus.clear()
    }
}
