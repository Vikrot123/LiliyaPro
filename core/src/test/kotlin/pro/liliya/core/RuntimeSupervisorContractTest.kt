package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor
import pro.liliya.core.runtime.RuntimeRecoveryPolicy

class RuntimeSupervisorContractTest {

    private class RestartableService : RuntimeService {

        override val name = "restartable"

        override var state = RuntimeServiceState.CREATED
            private set

        var starts = 0

        override fun start() {
            starts++
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }


    @Test
    fun supervisor_recovers_service_and_tracks_restart_count() {

        val registry = RuntimeServiceRegistry()
        val service = RestartableService()

        registry.register(service)

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        supervisor.start()

        assertEquals(RuntimeServiceState.RUNNING, service.state)

        val recovered = supervisor.recover("restartable")

        assertTrue(recovered)
        assertEquals(1, supervisor.getRestartCount("restartable"))
        assertEquals(2, service.starts)
    }


    @Test
    fun supervisor_blocks_recovery_after_retry_limit() {

        val registry = RuntimeServiceRegistry()
        val service = RestartableService()

        registry.register(service)

        val supervisor = RuntimeSupervisor(
                               registryProvider = { registry },
                               policy = RuntimeRecoveryPolicy(maxRetries = 1)
                     )

        supervisor.start()

        assertTrue(supervisor.recover("restartable"))
        assertFalse(supervisor.recover("restartable"))

        assertEquals(1, supervisor.getRestartCount("restartable"))
    }
}
