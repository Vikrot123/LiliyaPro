package pro.liliya.core

import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerConcurrentRecoveryContractTest {

    @Test
    fun concurrent_failures_do_not_double_recover_while_recovery_is_running() {

        RuntimeEventBus.clear()

        val recoveryStarted = CountDownLatch(1)
        val releaseRecovery = CountDownLatch(1)

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "concurrent-service"

            override var state = RuntimeServiceState.CREATED

            override fun start() {
                recoveryStarted.countDown()
                releaseRecovery.await()
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

        recoveryStarted.await()

        val t2 = thread {
            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeServiceFailed(
                    serviceName = "concurrent-service",
                    reason = "failure-2",
                    sourceRegistry = registry
                )
            )
        }

        t2.join()

        assertEquals(
            1,
            supervisor.getRestartCount("concurrent-service")
        )

        releaseRecovery.countDown()

        t1.join()

        assertEquals(
            1,
            supervisor.getRestartCount("concurrent-service")
        )

        RuntimeEventBus.clear()
    }
}
