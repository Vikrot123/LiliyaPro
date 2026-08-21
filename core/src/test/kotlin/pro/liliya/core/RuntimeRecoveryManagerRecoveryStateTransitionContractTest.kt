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

class RuntimeRecoveryManagerRecoveryStateTransitionContractTest {

    @Test
    fun successful_recovery_emits_started_then_completed_and_updates_snapshot() {

        val registry = RuntimeServiceRegistry()

        registry.register(TestService())

        val supervisor = RuntimeSupervisor(
            registryProvider = { registry }
        )

        val eventBus = RuntimeRecoveryEventBus()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        eventBus.subscribe {
            events += it
        }

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry,
            eventBus
        )

        val result = manager.recover("service")

        assertEquals(true, result)

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("service"),
                RuntimeRecoveryEvent.Completed("service")
            ),
            events
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
