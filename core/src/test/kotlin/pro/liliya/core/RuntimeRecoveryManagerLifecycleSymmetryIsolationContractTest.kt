package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerLifecycleSymmetryIsolationContractTest {

    @Test
    fun lifecycle_cycle_does_not_leak_recovery_state() {
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

        manager.reset()

        val snapshot = manager.snapshot()

        assertNull(snapshot.lastRecoveredService)
        assertNull(snapshot.lastRecoverySuccessful)

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "lifecycle-service",
                reason = "second",
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
