package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerSnapshotConsistencyContractTest {

    @Test
    fun successful_recovery_updates_snapshot_state() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val service = object : RuntimeService {
            override val name = "snapshot-service"

            override var state = RuntimeServiceState.CREATED

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

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry
        )

        val recovered = manager.recover("snapshot-service")

        assertEquals(true, recovered)

        val snapshot = manager.snapshot()

        assertEquals(
            "snapshot-service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )

        RuntimeEventBus.clear()
    }
}
