package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor

class RuntimeRecoveryManagerFailureEventLoopPreventionContractTest {

    @Test
    fun recovery_completion_does_not_trigger_second_recovery_cycle() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        val service = TestService()

        registry.register(service)

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val manager = RuntimeRecoveryManager(supervisor)

        var recoveryAttempts = 0

        val observer: (RuntimeEvent) -> Unit = { event ->
            if (event is RuntimeEvent.RuntimeServiceFailed) {
                recoveryAttempts++
            }
        }

        RuntimeEventBus.subscribe(observer)

        manager.install()

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "service",
                reason = "test failure"
            )
        )

        assertEquals(
            1,
            recoveryAttempts
        )

        assertEquals(
            1,
            supervisor.getRestartCount("service")
        )

        val snapshot = manager.snapshot()

        assertEquals(
            "service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )

        manager.uninstall()
        RuntimeEventBus.unsubscribe(observer)
        RuntimeEventBus.clear()
    }


    private class TestService : RuntimeService {

        override val name = "service"

        override var state =
            RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
