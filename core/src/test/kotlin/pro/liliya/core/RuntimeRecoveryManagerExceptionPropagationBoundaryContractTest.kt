package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerExceptionPropagationBoundaryContractTest {

    @Test
    fun recovery_exception_does_not_escape_manager_boundary() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "exception-service"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                throw IllegalStateException("recovery failure")
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

        val result = manager.recover("exception-service")

        assertFalse(result)

        val snapshot = manager.snapshot()

        assertTrue(
            snapshot.lastRecoveredService == "exception-service"
        )

        RuntimeEventBus.clear()
    }
}
