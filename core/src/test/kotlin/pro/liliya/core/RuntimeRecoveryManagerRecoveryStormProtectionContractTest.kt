package pro.liliya.core

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerRecoveryStormProtectionContractTest {

    @Test
    fun recovery_storm_does_not_duplicate_restart_execution() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "storm-service"
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

        val managerA = RuntimeRecoveryManager(
            supervisor,
            registry
        )

        val managerB = RuntimeRecoveryManager(
            supervisor,
            registry
        )

        managerA.install()
        managerB.install()

        val threads = (1..10).map { index ->
            thread {
                RuntimeEventBus.publish(
                    RuntimeEvent.RuntimeServiceFailed(
                        serviceName = "storm-service",
                        reason = "storm-$index",
                        sourceRegistry = registry
                    )
                )
            }
        }

        threads.forEach { it.join() }

        assertEquals(
            1,
            supervisor.getRestartCount("storm-service")
        )

        RuntimeEventBus.clear()
    }
}
