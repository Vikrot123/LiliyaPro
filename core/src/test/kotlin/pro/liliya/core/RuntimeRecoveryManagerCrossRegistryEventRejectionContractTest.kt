package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerCrossRegistryEventRejectionContractTest {

    @Test
    fun events_from_foreign_registry_are_rejected() {
        RuntimeEventBus.clear()

        val registryA = RuntimeServiceRegistry()
        val registryB = RuntimeServiceRegistry()

        registryA.register(object : RuntimeService {
            override val name = "shared-service"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        registryB.register(object : RuntimeService {
            override val name = "shared-service"
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
                serviceName = "shared-service",
                reason = "foreign-registry-event",
                sourceRegistry = registryB
            )
        )

        assertEquals(
            0,
            supervisorA.getRestartCount("shared-service")
        )

        assertEquals(
            1,
            supervisorB.getRestartCount("shared-service")
        )

        RuntimeEventBus.clear()
    }
}
