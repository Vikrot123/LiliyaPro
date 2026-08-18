package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeSupervisorResetLifecycleContractTest {

    @Test
    fun supervisor_reset_clears_restart_history() {
        val registry = RuntimeServiceRegistry()
        val service = TestService()

        registry.register(service)

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        supervisor.start()

        assertTrue(supervisor.recover("reset-service"))

        assertEquals(
            1,
            supervisor.getRestartCount("reset-service")
        )

        supervisor.reset()

        assertEquals(
            0,
            supervisor.getRestartCount("reset-service")
        )
    }

    private class TestService : RuntimeService {
        override val name = "reset-service"

        override var state = RuntimeServiceState.CREATED
            private set

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
