package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerLifecycleOrderingContractTest {

    @Test
    fun recover_emits_started_then_completed_in_order() {

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

        val result = manager.recover("service-A")

        assertEquals(true, result)

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("service-A"),
                RuntimeRecoveryEvent.Completed("service-A")
            ),
            events
        )
    }
}
