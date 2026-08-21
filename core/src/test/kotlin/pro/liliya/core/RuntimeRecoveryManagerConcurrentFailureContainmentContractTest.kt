package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerConcurrentFailureContainmentContractTest {

    @Test
    fun one_failed_recovery_does_not_break_other_recoveries() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "failing-service"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                throw IllegalStateException("expected failure")
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        registry.register(object : RuntimeService {
            override val name = "healthy-service"
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

        val failed = thread {
            manager.recover("failing-service")
        }

        val healthy = thread {
            manager.recover("healthy-service")
        }

        failed.join()
        healthy.join()

        val snapshot = manager.snapshot()

        assertEquals(
            1,
            snapshot.restartCounts["healthy-service"]
        )

        RuntimeEventBus.clear()
    }
}
