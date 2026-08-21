package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.RuntimeRecoveryManager
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceRegistry
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.RuntimeSupervisor
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeRecoveryManagerDuplicateExecutionContractTest {

    @Test
    fun duplicate_recovery_requests_are_executed_as_independent_recoveries() {

        val recoveryEventBus = RuntimeRecoveryEventBus()

        val registry = RuntimeServiceRegistry()

        registry.register(
            TestService()
        )

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry,
            recoveryEventBus
        )

        val events = mutableListOf<String>()

        recoveryEventBus.subscribe { event ->
            when (event) {
                is RuntimeRecoveryEvent.Started ->
                    events.add("started")

                is RuntimeRecoveryEvent.Completed ->
                    events.add("completed")

                is RuntimeRecoveryEvent.Failed ->
                    events.add("failed")
            }
        }

        manager.recover("service")
        manager.recover("service")

        assertEquals(
            listOf(
                "started",
                "completed",
                "started",
                "completed"
            ),
            events
        )

        assertEquals(
            2,
            supervisor.getRestartCount("service")
        )

        recoveryEventBus.clear()
    }


    private class TestService : RuntimeService {

        override val name = "service"

        override var state = RuntimeServiceState.CREATED

        override fun start() {
            state = RuntimeServiceState.RUNNING
        }

        override fun stop() {
            state = RuntimeServiceState.STOPPED
        }
    }
}
