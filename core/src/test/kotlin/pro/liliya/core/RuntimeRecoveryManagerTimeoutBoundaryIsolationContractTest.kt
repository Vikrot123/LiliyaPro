package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerTimeoutBoundaryIsolationContractTest {

    @Test
    fun recovery_timeout_does_not_leave_manager_locked() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "timeout-service"
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

        val first = manager.recover("timeout-service")

        val snapshotAfterFirst = manager.snapshot()

        val second = manager.recover("timeout-service")

        val snapshotAfterSecond = manager.snapshot()

        assertTrue(first)
        assertTrue(second)

        assertEquals(
            2,
            snapshotAfterSecond.restartCounts["timeout-service"]
        )

        assertEquals(
            "timeout-service",
            snapshotAfterSecond.lastRecoveredService
        )

        assertEquals(
            snapshotAfterFirst.lastRecoverySuccessful,
            true
        )

        RuntimeEventBus.clear()
    }
}
