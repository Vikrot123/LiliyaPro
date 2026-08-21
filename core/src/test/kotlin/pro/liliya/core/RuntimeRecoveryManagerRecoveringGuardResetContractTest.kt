package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerRecoveringGuardResetContractTest {

    @Test
    fun reset_clears_active_recovery_guard() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "guard-service"

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
                serviceName = "guard-service",
                reason = "failure",
                sourceRegistry = registry
            )
        )

        manager.reset()

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "guard-service",
                reason = "after-reset",
                sourceRegistry = registry
            )
        )

        val snapshot = manager.snapshot()

        assertEquals(
            "guard-service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )

        RuntimeEventBus.clear()
    }
}
