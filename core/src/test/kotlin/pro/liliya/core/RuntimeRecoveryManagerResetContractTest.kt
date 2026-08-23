package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerResetContractTest {

    @Test
    fun reset_clears_recovery_state() {

        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "reset-service"
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
                serviceName = "reset-service",
                reason = "first",
                sourceRegistry = registry
            )
        )

        assertEquals(
            "reset-service",
            manager.snapshot().lastRecoveredService
        )

        manager.reset()

        val snapshot = manager.snapshot()

        assertNull(
            snapshot.lastRecoveredService
        )

        assertNull(
            snapshot.lastRecoverySuccessful
        )

        RuntimeEventBus.clear()
    }
}
