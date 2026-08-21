package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerEventReplayIsolationContractTest {

    @Test
    fun old_recovery_events_are_not_replayed_after_new_subscription() {
        RuntimeEventBus.clear()

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "replay-service"
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

        val recoveryBus = RuntimeRecoveryEventBus()

        val manager = RuntimeRecoveryManager(
            supervisor,
            registry,
            recoveryBus
        )

        manager.install()

        manager.recover("replay-service")

        val lateEvents = mutableListOf<RuntimeRecoveryEvent>()

        recoveryBus.subscribe {
            lateEvents.add(it)
        }

        assertEquals(
            emptyList(),
            lateEvents
        )

        RuntimeEventBus.clear()
    }
}
