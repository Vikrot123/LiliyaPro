package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerResetEventIsolationContractTest {

    @Test
    fun reset_removes_old_recovery_event_lifecycle() {
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val recoveryBus = RuntimeRecoveryEventBus()

        recoveryBus.subscribe {
            events.add(it)
        }

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "reset-event-service"
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

        manager.install()

        manager.recover("reset-event-service")

        val firstSize = events.size

        manager.reset()

        manager.install()

        manager.recover("reset-event-service")

        assertEquals(
            firstSize * 2,
            events.size
        )

        RuntimeEventBus.clear()
    }
}
