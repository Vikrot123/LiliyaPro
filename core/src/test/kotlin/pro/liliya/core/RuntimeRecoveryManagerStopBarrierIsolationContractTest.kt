package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerStopBarrierIsolationContractTest {

    @Test
    fun stop_barrier_blocks_recovery_until_reinstall() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "stop-barrier-service"
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
                serviceName = "stop-barrier-service",
                reason = "before-stop",
                sourceRegistry = registry
            )
        )

        val beforeStop =
            supervisor.getRestartCount("stop-barrier-service")

        manager.uninstall()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "stop-barrier-service",
                reason = "after-stop",
                sourceRegistry = registry
            )
        )

        assertEquals(
            beforeStop,
            supervisor.getRestartCount("stop-barrier-service")
        )

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "stop-barrier-service",
                reason = "after-reinstall",
                sourceRegistry = registry
            )
        )

        assertEquals(
            beforeStop + 1,
            supervisor.getRestartCount("stop-barrier-service")
        )

        RuntimeEventBus.clear()
    }
}
