package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerRecoveryLockReleaseGuaranteeContractTest {

    @Test
    fun failed_recovery_releases_global_recovery_lock() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "lock-release-service"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                throw IllegalStateException("forced recovery failure")
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
                serviceName = "lock-release-service",
                reason = "first-failure",
                sourceRegistry = registry
            )
        )

        val first =
            supervisor.getRestartCount("lock-release-service")

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "lock-release-service",
                reason = "second-failure",
                sourceRegistry = registry
            )
        )

        val second =
            supervisor.getRestartCount("lock-release-service")

        assertEquals(
            2,
            second
        )

        assertEquals(
            first + 1,
            second
        )

        RuntimeEventBus.clear()
    }
}
