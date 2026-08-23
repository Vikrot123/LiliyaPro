package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerFailureSnapshotContractTest {

    @Test
    fun failed_recovery_updates_snapshot_state() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "failure-snapshot-service"

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

        val recovered = manager.recover(
            "missing-service"
        )

        assertEquals(
            false,
            recovered
        )

        val snapshot = manager.snapshot()

        assertEquals(
            "missing-service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            false,
            snapshot.lastRecoverySuccessful
        )

        RuntimeEventBus.clear()
    }
}
