package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor

class RuntimeRecoveryManagerRecoveryResetContractTest {

    @Test
    fun successful_recovery_state_is_cleared_after_reset() {

        val registry = RuntimeServiceRegistry()

        registry.register(
            TestService()
        )

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val manager = RuntimeRecoveryManager(supervisor)

        val recovered = manager.recover("service")

        assertEquals(
            true,
            recovered
        )

        var snapshot = manager.snapshot()

        assertEquals(
            "service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )

        manager.reset()

        snapshot = manager.snapshot()

        assertNull(
            snapshot.lastRecoveredService
        )

        assertNull(
            snapshot.lastRecoverySuccessful
        )
    }


    private class TestService : RuntimeService {

        override val name = "service"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
