package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerMultiManagerResetIsolationContractTest {

    @Test
    fun reset_of_one_manager_does_not_break_other_manager_isolation() {
        RuntimeEventBus.clear()

        val registryA = RuntimeServiceRegistry()
        val registryB = RuntimeServiceRegistry()

        registryA.register(object : RuntimeService {
            override val name = "service-a"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        registryB.register(object : RuntimeService {
            override val name = "service-b"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        val supervisorA = RuntimeSupervisor(
            registryProvider = { registryA }
        )

        val supervisorB = RuntimeSupervisor(
            registryProvider = { registryB }
        )

        val managerA = RuntimeRecoveryManager(
            supervisorA,
            registryA
        )

        val managerB = RuntimeRecoveryManager(
            supervisorB,
            registryB
        )

        managerA.install()
        managerB.install()

        managerA.reset()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service-b",
                reason = "after-a-reset",
                sourceRegistry = registryB
            )
        )

        assertEquals(
            0,
            supervisorA.getRestartCount("service-a")
        )

        assertEquals(
            1,
            supervisorB.getRestartCount("service-b")
        )

        RuntimeEventBus.clear()
    }
}
