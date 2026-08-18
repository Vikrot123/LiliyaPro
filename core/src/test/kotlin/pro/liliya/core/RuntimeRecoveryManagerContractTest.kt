package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor

class RuntimeRecoveryManagerContractTest {

    private fun createManager(): Pair<RuntimeRecoveryManager, RuntimeSupervisor> {

        val registry = RuntimeServiceRegistry()

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        return RuntimeRecoveryManager(supervisor) to supervisor
    }


    @Test
    fun recoveryManager_double_install_does_not_duplicate_listener() {

        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val service = object : RuntimeService {

            override val name = "test-service"

            override var state = RuntimeServiceState.CREATED
                private set

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

        val manager = RuntimeRecoveryManager(supervisor)

        manager.install()
        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "test-service",
                reason = "duplicate"
            )
        )

        assertEquals(
            1,
            supervisor.getRestartCount("test-service")
        )

        manager.uninstall()

        RuntimeEventBus.clear()
    }


    @Test
    fun recoveryManager_uninstall_stops_event_processing() {

        RuntimeEventBus.clear()

        val (manager, supervisor) = createManager()

        manager.install()
        manager.uninstall()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "test-service",
                reason = "after uninstall"
            )
        )

        assertEquals(
            0,
            supervisor.getRestartCount("test-service")
        )

        RuntimeEventBus.clear()
    }


    @Test
    fun recoveryManager_reset_clears_snapshot_state() {

        RuntimeEventBus.clear()

        val (manager, _) = createManager()

        manager.install()

        manager.reset()

        val snapshot = manager.snapshot()

        assertNull(snapshot.lastRecoveredService)
        assertNull(snapshot.lastRecoverySuccessful)

        RuntimeEventBus.clear()
    }
}
