package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEvent
import pro.liliya.core.runtime.recovery.RuntimeRecoveryEventBus

class RuntimeRecoveryManagerFailureStateTransitionContractTest {

    @Test
    fun failed_recovery_emits_started_then_failed_and_updates_snapshot() {
        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "failed-service"

            override var state = RuntimeServiceState.CREATED

            override fun start() {
                throw IllegalStateException("restart failed")
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        })

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

        val result = manager.recover("failed-service")

        assertEquals(false, result)

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("failed-service"),
                RuntimeRecoveryEvent.Failed("failed-service")
            ),
            events
        )

        val snapshot = manager.snapshot()

        assertEquals(
            "failed-service",
            snapshot.lastRecoveredService
        )

        assertEquals(
            false,
            snapshot.lastRecoverySuccessful
        )
    }
}
