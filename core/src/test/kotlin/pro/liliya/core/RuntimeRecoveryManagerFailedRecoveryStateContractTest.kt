package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor

class RuntimeRecoveryManagerFailedRecoveryStateContractTest {

    @Test
    fun failed_recovery_updates_manager_state_and_next_success_replaces_it() {
        val registry = RuntimeServiceRegistry()

        val service = FlakyService()

        registry.register(service)

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val manager = RuntimeRecoveryManager(supervisor)

        service.failNextStart = true

        val failed = manager.recover("service")

        assertEquals(false, failed)

        var snapshot = manager.snapshot()

        assertEquals(
            "service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            false,
            snapshot.lastRecoverySuccessful
        )

        service.failNextStart = false

        val recovered = manager.recover("service")

        assertEquals(
            true,
            recovered
        )

        snapshot = manager.snapshot()

        assertEquals(
            "service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )
    }

    private class FlakyService : RuntimeService {

        override val name = "service"

        override var state =
            RuntimeServiceState.CREATED

        var failNextStart = false

        override fun start() {
            if (failNextStart) {
                throw IllegalStateException("start failed")
            }

            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
