package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeRecoveryPolicy
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor

class RuntimeRecoveryManagerContractTest {

    private class FailingRestartService : RuntimeService {

        override val name = "recovery-service"

        override var state = RuntimeServiceState.CREATED
            private set

        var starts = 0

        override fun start() {
            starts++

            if (starts == 1) {
                throw IllegalStateException("initial failure")
            }

            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }


    @Test
    fun recoveryManager_restarts_failed_service() {

        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val service = FailingRestartService()

        registry.register(service)

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val recoveryManager = RuntimeRecoveryManager(
            supervisor
        )

        recoveryManager.install()

        try {
            service.start()
        } catch (_: Exception) {
        }

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "recovery-service",
                reason = "test failure"
            )
        )

        assertTrue(
            service.starts > 0
        )

        assertEquals(
            1,
            supervisor.getRestartCount("recovery-service")
        )

        RuntimeEventBus.clear()
    }
}
