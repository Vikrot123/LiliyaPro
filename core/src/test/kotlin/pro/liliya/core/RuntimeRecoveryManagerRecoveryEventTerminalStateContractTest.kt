package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerRecoveryEventTerminalStateContractTest {

    @Test
    fun recovery_emits_single_terminal_event() {
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val recoveryBus = RuntimeRecoveryEventBus()

        recoveryBus.subscribe {
            events.add(it)
        }

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "terminal-event-service"
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
            registry,
            recoveryBus
        )

        manager.recover("terminal-event-service")

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("terminal-event-service"),
                RuntimeRecoveryEvent.Completed("terminal-event-service")
            ),
            events
        )

        RuntimeEventBus.clear()
    }
}
