package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor

class RuntimeRecoveryManagerInstalledOwnerIsolationContractTest {

    @Test
    fun reset_of_rejected_manager_must_not_release_active_manager_ownership() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(
            object : RuntimeService {
                override val name = "owned-service"

                override var state =
                    RuntimeServiceState.CREATED

                override fun start() {
                    state = RuntimeServiceState.RUNNING
                }

                override fun stop() {
                    state = RuntimeServiceState.STOPPED
                }
            }
        )

        val supervisor =
            RuntimeSupervisor(
                registryProvider = { registry }
            )

        val owner =
            RuntimeRecoveryManager(
                supervisor,
                registry
            )

        val rejected =
            RuntimeRecoveryManager(
                supervisor,
                registry
            )

        val third =
            RuntimeRecoveryManager(
                supervisor,
                registry
            )

        try {
            owner.install()

            // Must be rejected because owner already owns this registry.
            rejected.install()

            // A rejected manager must not be able to release owner's slot.
            rejected.reset()

            // Must still be rejected while owner is installed.
            third.install()

            RuntimeEventBus.publish(
                RuntimeEvent.RuntimeServiceFailed(
                    serviceName = "owned-service",
                    reason = "failure",
                    sourceRegistry = registry
                )
            )

            assertEquals(
                1,
                supervisor.getRestartCount("owned-service"),
                "reset of a rejected manager must not release another manager's installed ownership"
            )
        } finally {
            owner.uninstall()
            rejected.uninstall()
            third.uninstall()
            RuntimeEventBus.clear()
        }
    }
}
