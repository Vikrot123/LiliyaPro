package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerRepeatedRecoveryIsolationContractTest {

    @Test
    fun repeated_recovery_cycles_are_isolated() {

        val registry = RuntimeServiceRegistry()

        registry.register(
            object : RuntimeService {

                override val name = "service-A"

                override val state =
                    RuntimeServiceState.RUNNING

                override fun start() {
                }

                override fun stop() {
                }
            }
        )

        val supervisor = RuntimeSupervisor(
            registryProvider = {
                registry
            }
        )

        val recoveryBus = RuntimeRecoveryEventBus()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        recoveryBus.subscribe {
            events.add(it)
        }

        val manager = RuntimeRecoveryManager(
            supervisor = supervisor,
            recoveryEventBus = recoveryBus
        )

        assertEquals(
            true,
            manager.recover("service-A")
        )

        assertEquals(
            true,
            manager.recover("service-A")
        )

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("service-A"),
                RuntimeRecoveryEvent.Completed("service-A"),
                RuntimeRecoveryEvent.Started("service-A"),
                RuntimeRecoveryEvent.Completed("service-A")
            ),
            events
        )

        assertEquals(
            2,
            supervisor.getRestartCount("service-A")
        )

        val snapshot = manager.snapshot()

        assertEquals(
            "service-A",
            snapshot.lastRecoveredService
        )

        assertEquals(
            true,
            snapshot.lastRecoverySuccessful
        )
    }
}
