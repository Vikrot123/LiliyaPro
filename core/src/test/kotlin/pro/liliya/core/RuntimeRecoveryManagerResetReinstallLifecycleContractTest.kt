package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerResetReinstallLifecycleContractTest {

    @Test
    fun reset_allows_clean_reinstall_without_duplicate_recovery() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "reset-reinstall-service"
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

        manager.reset()

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "reset-reinstall-service",
                reason = "after-reset",
                sourceRegistry = registry
            )
        )

        assertEquals(
            1,
            supervisor.getRestartCount("reset-reinstall-service")
        )

        RuntimeEventBus.clear()
    }
}
