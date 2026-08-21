package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*

class RuntimeRecoveryManagerUninstallIsolationContractTest {

    @Test
    fun uninstall_blocks_recovery_until_reinstall() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "uninstall-service"

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

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "uninstall-service",
                reason = "before uninstall",
                sourceRegistry = registry
            )
        )

        val firstCount = supervisor.getRestartCount("uninstall-service")

        manager.uninstall()

        val beforeReinstall = manager.snapshot()

        assertEquals(
            "uninstall-service",
            beforeReinstall.lastRecoveredService
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "uninstall-service",
                reason = "after uninstall",
                sourceRegistry = registry
            )
        )

        val afterUninstall = manager.snapshot()

        assertEquals(
            firstCount,
            supervisor.getRestartCount("uninstall-service")
        )

        assertEquals(
            "uninstall-service",
            afterUninstall.lastRecoveredService
        )

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "uninstall-service",
                reason = "after reinstall",
                sourceRegistry = registry
            )
        )

        val afterReinstall = manager.snapshot()

        assertEquals(
            "uninstall-service",
            afterReinstall.lastRecoveredService
        )

        assertEquals(
            true,
            afterReinstall.lastRecoverySuccessful
        )

        RuntimeEventBus.clear()
    }
}
