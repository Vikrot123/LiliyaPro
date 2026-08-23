package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerCompositionIsolationContractTest {

    @Test
    fun different_registries_do_not_recover_each_other_services() {

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

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service-a",
                reason = "failure",
                sourceRegistry = registryA
            )
        )

        assertEquals(
            1,
            supervisorA.getRestartCount("service-a")
        )

        assertEquals(
            0,
            supervisorB.getRestartCount("service-a")
        )

        RuntimeEventBus.clear()
    }
}
