package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerStaleEventIsolationContractTest {

    @Test
    fun stale_events_after_uninstall_do_not_trigger_recovery() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "stale-event-service"

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
                serviceName = "stale-event-service",
                reason = "before-uninstall",
                sourceRegistry = registry
            )
        )

        val beforeUninstall =
            supervisor.getRestartCount("stale-event-service")

        manager.uninstall()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "stale-event-service",
                reason = "stale-event",
                sourceRegistry = registry
            )
        )

        assertEquals(
            beforeUninstall,
            supervisor.getRestartCount("stale-event-service")
        )

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "stale-event-service",
                reason = "after-reinstall",
                sourceRegistry = registry
            )
        )

        assertEquals(
            beforeUninstall + 1,
            supervisor.getRestartCount("stale-event-service")
        )

        RuntimeEventBus.clear()
    }
}
