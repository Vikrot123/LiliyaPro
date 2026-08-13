package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeRecoverySnapshotContractTest {

    @Test
    fun recoverySnapshot_reports_last_recovery() {

        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val service = object : RuntimeService {

            override val name = "snapshot-service"

            override var state =
                RuntimeServiceState.CREATED
                private set

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        }

        registry.register(service)

        val supervisor =
            RuntimeSupervisor(
                registryProvider = { registry }
            )

        val manager =
            RuntimeRecoveryManager(supervisor)

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "snapshot-service",
                reason = "test failure"
            )
        )

        val snapshot =
            manager.snapshot()

        assertEquals(
            "snapshot-service",
            snapshot.lastRecoveredService
        )

        assertTrue(
            snapshot.lastRecoverySuccessful == true
        )

        assertEquals(
            1,
            snapshot.restartCounts["snapshot-service"]
        )

        RuntimeEventBus.clear()
    }
}
