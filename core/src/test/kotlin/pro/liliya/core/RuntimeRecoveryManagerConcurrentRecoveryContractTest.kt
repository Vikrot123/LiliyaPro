package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerConcurrentRecoveryContractTest {

    @Test
    fun concurrent_failures_do_not_double_recover() {

        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "concurrent-service"
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

        val t1 = thread {
            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeServiceFailed(
                    serviceName = "concurrent-service",
                    reason = "failure-1",
                    sourceRegistry = registry
                )
            )
        }

        val t2 = thread {
            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeServiceFailed(
                    serviceName = "concurrent-service",
                    reason = "failure-2",
                    sourceRegistry = registry
                )
            )
        }

        t1.join()
        t2.join()

        assertEquals(
            1,
            supervisor.getRestartCount("concurrent-service")
        )

        RuntimeEventBus.clear()
    }
}
