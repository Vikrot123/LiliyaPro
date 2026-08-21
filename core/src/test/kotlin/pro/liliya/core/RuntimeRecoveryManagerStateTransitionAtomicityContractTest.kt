package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerStateTransitionAtomicityContractTest {

    @Test
    fun recovery_state_transition_is_atomic() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "atomic-service-a"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        registry.register(object : RuntimeService {
            override val name = "atomic-service-b"
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

        val t1 = thread {
            manager.recover("atomic-service-a")
        }

        val t2 = thread {
            manager.recover("atomic-service-b")
        }

        t1.join()
        t2.join()

        val snapshot = manager.snapshot()

        assertNotNull(snapshot.lastRecoveredService)
        assertNotNull(snapshot.lastRecoverySuccessful)

        RuntimeEventBus.clear()
    }
}
