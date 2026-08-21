package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerMemoryVisibilityIsolationContractTest {

    @Test
    fun completed_recovery_state_is_visible_across_threads() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "visibility-service"
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

        val recoverThread = thread {
            manager.recover("visibility-service")
        }

        recoverThread.join()

        val snapshotThreadResult = arrayOfNulls<String>(1)

        val snapshotThread = thread {
            snapshotThreadResult[0] =
                manager.snapshot().lastRecoveredService
        }

        snapshotThread.join()

        assertEquals(
            "visibility-service",
            snapshotThreadResult[0]
        )

        RuntimeEventBus.clear()
    }
}
