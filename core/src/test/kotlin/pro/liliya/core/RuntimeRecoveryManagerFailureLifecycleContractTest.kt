package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerFailureLifecycleContractTest {

    @Test
    fun recover_emits_started_then_failed_when_service_restart_fails() {

        val registry = RuntimeServiceRegistry()

        registry.register(
            object : RuntimeService {

                override val name = "broken-service"

                override val state =
                    RuntimeServiceState.RUNNING

                override fun start() {
                    throw IllegalStateException("boom")
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

        val result = manager.recover("broken-service")

        assertEquals(false, result)

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("broken-service"),
                RuntimeRecoveryEvent.Failed("broken-service")
            ),
            events
        )
    }
}
