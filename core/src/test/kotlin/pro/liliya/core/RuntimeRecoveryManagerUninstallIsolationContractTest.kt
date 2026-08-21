package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerUninstallIsolationContractTest {

    @Test
    fun uninstall_removes_only_target_manager_listener() {
        RuntimeEventBus.clear()

        val registryA = RuntimeServiceRegistry()
        val registryB = RuntimeServiceRegistry()

        registryA.register(object : RuntimeService {
            override val name = "uninstall-service-a"
            override var state = RuntimeServiceState.CREATED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

        registryB.register(object : RuntimeService {
            override val name = "uninstall-service-b"
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

        managerA.uninstall()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "uninstall-service-a",
                reason = "after-uninstall",
                sourceRegistry = registryA
            )
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "uninstall-service-b",
                reason = "still-active",
                sourceRegistry = registryB
            )
        )

        assertEquals(
            0,
            supervisorA.getRestartCount("uninstall-service-a")
        )

        assertEquals(
            1,
            supervisorB.getRestartCount("uninstall-service-b")
        )

        RuntimeEventBus.clear()
    }
}
