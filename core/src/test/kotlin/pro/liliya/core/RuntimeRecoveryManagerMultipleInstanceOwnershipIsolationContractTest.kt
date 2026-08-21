package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerMultipleInstanceOwnershipIsolationContractTest {

    @Test
    fun multiple_manager_instances_do_not_duplicate_recovery_execution() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "ownership-service"

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

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "ownership-service",
                reason = "ownership-test",
                sourceRegistry = registry
            )
        )

        assertEquals(
            1,
            supervisor.getRestartCount("ownership-service")
        )

        RuntimeEventBus.clear()
    }
}
