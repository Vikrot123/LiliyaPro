package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerSnapshotConsistencyIsolationContractTest {

    @Test
    fun snapshot_state_is_consistent_across_lifecycle() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "snapshot-service"
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
                serviceName = "snapshot-service",
                reason = "snapshot-test",
                sourceRegistry = registry
            )
        )

        val first = manager.snapshot()
        val second = manager.snapshot()

        assertEquals(
            first.lastRecoveredService,
            second.lastRecoveredService
        )

        assertEquals(
            first.lastRecoverySuccessful,
            second.lastRecoverySuccessful
        )

        assertEquals(
            1,
            first.restartCounts["snapshot-service"]
        )

        manager.reset()

        val reset = manager.snapshot()

        assertNull(reset.lastRecoveredService)
        assertNull(reset.lastRecoverySuccessful)

        RuntimeEventBus.clear()
    }
}
