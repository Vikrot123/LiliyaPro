package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerReentrancyIsolationContractTest {

    @Test
    fun recursive_recovery_request_is_contained() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "reentrant-service"
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

        val first = manager.recover("reentrant-service")
        val second = manager.recover("reentrant-service")

        val snapshot = manager.snapshot()

        assertTrue(first)
        assertTrue(second)

        assertEquals(
            2,
            snapshot.restartCounts["reentrant-service"]
        )

        assertEquals(
            "reentrant-service",
            snapshot.lastRecoveredService
        )

        RuntimeEventBus.clear()
    }
}
