package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.*
import pro.liliya.core.runtime.recovery.*

class RuntimeRecoveryManagerEventOrderingIsolationContractTest {

    @Test
    fun recovery_events_keep_order_and_do_not_duplicate() {
        RuntimeEventBus.clear()

        val events = mutableListOf<RuntimeRecoveryEvent>()

        val recoveryBus = RuntimeRecoveryEventBus()

        recoveryBus.subscribe {
            events.add(it)
        }

        val registry = RuntimeServiceRegistry()

        registry.register(object : RuntimeService {
            override val name = "ordering-service"
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

        manager.recover("ordering-service")

        assertEquals(
            listOf(
                RuntimeRecoveryEvent.Started("ordering-service"),
                RuntimeRecoveryEvent.Completed("ordering-service")
            ),
            events
        )

        RuntimeEventBus.clear()
    }
}
