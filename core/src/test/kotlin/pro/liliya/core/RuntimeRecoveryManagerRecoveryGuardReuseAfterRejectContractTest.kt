package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerRecoveryGuardReuseAfterRejectContractTest {

    @Test
    fun rejected_recovery_does_not_poison_future_recovery() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "guard-reuse-service"
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

        val first = thread {
            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeServiceFailed(
                    serviceName = "guard-reuse-service",
                    reason = "first",
                    sourceRegistry = registry
                )
            )
        }

        val second = thread {
            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeServiceFailed(
                    serviceName = "guard-reuse-service",
                    reason = "second",
                    sourceRegistry = registry
                )
            )
        }

        first.join()
        second.join()

        val afterConcurrent =
            supervisor.getRestartCount("guard-reuse-service")

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "guard-reuse-service",
                reason = "after-release",
                sourceRegistry = registry
            )
        )

        val afterReuse =
            supervisor.getRestartCount("guard-reuse-service")

        assertEquals(
            afterConcurrent + 1,
            afterReuse
        )

        RuntimeEventBus.clear()
    }
}
